package com.asmj.marketplace.masterdata;

import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MasterDataCsvController {
    private static final String PREVIEW_CSV = "MASTER_DATA_CSV_PREVIEW";
    private static final String PREVIEW_NAME = "MASTER_DATA_CSV_NAME";
    private static final int MAX_BYTES = 2 * 1024 * 1024;
    private static final int MAX_ROWS = 5000;
    private static final List<String> HEADERS = List.of("groupKey", "code", "label", "parentCode", "active", "sortOrder");
    private final MasterDataOptionRepository options;
    private final MasterDataGroupRepository groups;
    private final MasterDataImportLogRepository logs;

    public MasterDataCsvController(MasterDataOptionRepository options, MasterDataGroupRepository groups, MasterDataImportLogRepository logs) {
        this.options = options; this.groups = groups; this.logs = logs;
    }

    @GetMapping("/admin/master-data/export.csv")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> export(@RequestParam(required = false) String group) {
        String selected = clean(group).toUpperCase(Locale.ROOT);
        List<MasterDataOption> rows = options.findAllByOrderByGroupKeyAscSortOrderAscLabelAsc().stream()
                .filter(o -> selected.isBlank() || selected.equals(o.getGroupKey())).toList();
        StringBuilder out = new StringBuilder(String.join(",", HEADERS)).append("\r\n");
        for (MasterDataOption o : rows) {
            out.append(csv(o.getGroupKey())).append(',').append(csv(o.getCode())).append(',').append(csv(o.getLabel())).append(',')
                    .append(csv(o.getParentCode())).append(',').append(o.isActive()).append(',').append(o.getSortOrder()).append("\r\n");
        }
        String filename = selected.isBlank() ? "asmj-master-data.csv" : "asmj-master-data-" + selected.toLowerCase(Locale.ROOT) + ".csv";
        return org.springframework.http.ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(out.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/admin/master-data/template.csv")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> template() {
        String sample = String.join(",", HEADERS) + "\r\n" +
                "INDIA_STATE,OD,Odisha,,true,1\r\n" +
                "INDIA_CITY,BBSR,Bhubaneswar,OD,true,1\r\n";
        return org.springframework.http.ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"asmj-master-data-template.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(sample.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/admin/master-data/import/preview")
    public String preview(@RequestParam("file") MultipartFile file, HttpSession session, Model model, RedirectAttributes ra) {
        try {
            if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a CSV file first.");
            if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("CSV file must be 2 MB or smaller.");
            String raw = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<List<String>> parsed = parseCsv(raw);
            if (parsed.size() < 2) throw new IllegalArgumentException("The CSV must contain the header and at least one data row.");
            if (parsed.size() - 1 > MAX_ROWS) throw new IllegalArgumentException("A maximum of 5,000 data rows can be imported at a time.");
            List<String> header = parsed.get(0).stream().map(String::trim).toList();
            if (!header.equals(HEADERS)) throw new IllegalArgumentException("CSV headers must be exactly: " + String.join(",", HEADERS));
            List<ImportRow> rows = new ArrayList<>(); List<String> errors = new ArrayList<>();
            Map<String, MasterDataGroup> groupMap = new HashMap<>(); groups.findAllByOrderByDisplayNameAsc().forEach(g -> groupMap.put(g.getGroupKey(), g));
            Map<String, MasterDataOption> existing = new HashMap<>(); options.findAll().forEach(o -> existing.put(key(o.getGroupKey(), o.getCode()), o));
            Set<String> seen = new HashSet<>();
            for (int i = 1; i < parsed.size(); i++) {
                List<String> c = parsed.get(i); int line = i + 1;
                if (c.size() != HEADERS.size()) { errors.add("Row " + line + ": expected 6 columns, found " + c.size() + "."); continue; }
                String group = c.get(0).trim().toUpperCase(Locale.ROOT), code = c.get(1).trim().toUpperCase(Locale.ROOT);
                String label = c.get(2).trim(), parent = c.get(3).trim().toUpperCase(Locale.ROOT);
                String activeText = c.get(4).trim().toLowerCase(Locale.ROOT), orderText = c.get(5).trim();
                boolean active = "true".equals(activeText) || "1".equals(activeText) || "yes".equals(activeText);
                if (!(Set.of("true", "false", "1", "0", "yes", "no").contains(activeText))) errors.add("Row " + line + ": active must be true/false.");
                int sort = 100; try { sort = Integer.parseInt(orderText); } catch (Exception ex) { errors.add("Row " + line + ": sortOrder must be a whole number."); }
                if (group.isBlank() || code.isBlank() || !code.matches("[A-Z0-9_-]{1,80}") || label.isBlank() || label.length() > 120) errors.add("Row " + line + ": groupKey, code and label are required; code may contain only letters, numbers, underscores or hyphens (max 80), and label max 120 characters.");
                MasterDataGroup g = groupMap.get(group);
                if (g == null || !g.isActive()) errors.add("Row " + line + ": unknown or inactive group " + group + ".");
                else if (g.getParentGroupKey() != null && !g.getParentGroupKey().isBlank() && parent.isBlank()) errors.add("Row " + line + ": parentCode is required for " + group + ".");
                else if ((g == null || g.getParentGroupKey() == null || g.getParentGroupKey().isBlank()) && !parent.isBlank()) errors.add("Row " + line + ": " + group + " does not use a parent group; leave parentCode blank.");
                String k = key(group, code);
                if (!seen.add(k)) errors.add("Row " + line + ": duplicate groupKey/code " + group + "/" + code + " in this file.");
                rows.add(new ImportRow(line, group, code, label, parent, active, sort, existing.containsKey(k)));
            }
            // Parent validation accepts an active parent already in the database or an active parent in this file.
            Map<String, ImportRow> incoming = new HashMap<>(); rows.forEach(r -> incoming.put(key(r.groupKey, r.code), r));
            for (ImportRow r : rows) {
                MasterDataGroup g = groupMap.get(r.groupKey);
                if (g == null || g.getParentGroupKey() == null || g.getParentGroupKey().isBlank() || r.parentCode.isBlank()) continue;
                String pk = key(g.getParentGroupKey(), r.parentCode); MasterDataOption dbParent = existing.get(pk); ImportRow csvParent = incoming.get(pk);
                boolean valid = csvParent != null ? csvParent.active : (dbParent != null && dbParent.isActive());
                if (!valid) errors.add("Row " + r.line + ": parent " + g.getParentGroupKey() + "/" + r.parentCode + " was not found as an active option.");
            }
            session.setAttribute(PREVIEW_CSV, raw); session.setAttribute(PREVIEW_NAME, safeName(file.getOriginalFilename()));
            model.addAttribute("previewRows", rows); model.addAttribute("importErrors", errors);
            model.addAttribute("newCount", rows.stream().filter(r -> !r.update).count()); model.addAttribute("updateCount", rows.stream().filter(r -> r.update).count());
            model.addAttribute("canImport", errors.isEmpty() && !rows.isEmpty());
            return "admin/master-data-import-preview";
        } catch (Exception ex) { ra.addFlashAttribute("errorMessage", message(ex)); return "redirect:/admin/master-data"; }
    }

    @PostMapping("/admin/master-data/import/confirm")
    public String confirm(HttpSession session, java.security.Principal principal, RedirectAttributes ra) {
        String raw = (String) session.getAttribute(PREVIEW_CSV); String filename = (String) session.getAttribute(PREVIEW_NAME);
        if (raw == null) { ra.addFlashAttribute("errorMessage", "Import preview expired. Upload the CSV again."); return "redirect:/admin/master-data"; }
        try {
            // Re-run validation at confirmation time to protect against stale previews and changed parent records.
            List<List<String>> parsed = parseCsv(raw);
            if (parsed.size() < 2 || parsed.size() - 1 > MAX_ROWS || !parsed.get(0).stream().map(String::trim).toList().equals(HEADERS)) throw new IllegalArgumentException("The saved CSV preview is invalid. Upload it again.");
            Map<String, MasterDataGroup> groupMap = new HashMap<>(); groups.findAllByOrderByDisplayNameAsc().forEach(g -> groupMap.put(g.getGroupKey(), g));
            Map<String, MasterDataOption> existing = new HashMap<>(); options.findAll().forEach(o -> existing.put(key(o.getGroupKey(), o.getCode()), o));
            List<ImportRow> rows = new ArrayList<>(); Set<String> seen = new HashSet<>();
            for (int i=1;i<parsed.size();i++) {
                List<String> c=parsed.get(i); if(c.size()!=6) throw new IllegalArgumentException("CSV row " +(i+1)+" has an invalid column count.");
                String group=c.get(0).trim().toUpperCase(Locale.ROOT), code=c.get(1).trim().toUpperCase(Locale.ROOT), label=c.get(2).trim(), parent=c.get(3).trim().toUpperCase(Locale.ROOT), a=c.get(4).trim().toLowerCase(Locale.ROOT);
                if(!Set.of("true","false","1","0","yes","no").contains(a)) throw new IllegalArgumentException("CSV row "+(i+1)+" has an invalid active value.");
                int sort; try{sort=Integer.parseInt(c.get(5).trim());}catch(Exception e){throw new IllegalArgumentException("CSV row "+(i+1)+" has an invalid sortOrder.");}
                MasterDataGroup g=groupMap.get(group); if(g==null||!g.isActive()||code.isBlank()||label.isBlank()||code.length()>80||label.length()>120) throw new IllegalArgumentException("CSV row "+(i+1)+" contains an invalid group, code or label.");
                if(!seen.add(key(group,code))) throw new IllegalArgumentException("Duplicate groupKey/code in CSV at row "+(i+1)+".");
                String parentGroup=clean(g.getParentGroupKey()); if(!parentGroup.isBlank()&&parent.isBlank())throw new IllegalArgumentException("CSV row "+(i+1)+" requires parentCode."); if(parentGroup.isBlank()&&!parent.isBlank())throw new IllegalArgumentException("CSV row "+(i+1)+" must not have parentCode.");
                rows.add(new ImportRow(i+1,group,code,label,parent,Set.of("true","1","yes").contains(a),sort,existing.containsKey(key(group,code))));
            }
            Map<String,ImportRow> incoming=new HashMap<>();rows.forEach(r->incoming.put(key(r.groupKey,r.code),r));
            for(ImportRow r:rows){MasterDataGroup g=groupMap.get(r.groupKey);String pg=clean(g.getParentGroupKey());if(pg.isBlank())continue;MasterDataOption db=existing.get(key(pg,r.parentCode));ImportRow in=incoming.get(key(pg,r.parentCode));if(!(in!=null?in.active:(db!=null&&db.isActive())))throw new IllegalArgumentException("Parent option missing/inactive for "+r.groupKey+"/"+r.code+".");}
            // Save parent groups first, then child groups; preserve Mongo IDs for updates.
            rows.sort(Comparator.comparingInt(r -> depth(r.groupKey, groupMap)));
            int inserted=0, updated=0;
            for(ImportRow r:rows){MasterDataOption o=existing.get(key(r.groupKey,r.code));if(o==null){o=new MasterDataOption();inserted++;}else updated++;
                o.setGroupKey(r.groupKey);o.setCode(r.code);o.setLabel(r.label);o.setParentCode(r.parentCode);o.setActive(r.active);o.setSortOrder(r.sortOrder);o.setUpdatedAt(Instant.now());o.setUpdatedBy(principal==null?"ADMIN":principal.getName());options.save(o);}
            MasterDataImportLog log=new MasterDataImportLog();log.setFileName(filename==null?"master-data.csv":filename);log.setImportedBy(principal==null?"ADMIN":principal.getName());log.setImportedAt(Instant.now());log.setInsertedCount(inserted);log.setUpdatedCount(updated);log.setRowCount(rows.size());logs.save(log);
            session.removeAttribute(PREVIEW_CSV);session.removeAttribute(PREVIEW_NAME);
            ra.addFlashAttribute("successMessage","CSV import completed: "+inserted+" new options and "+updated+" updated options. Existing options not included in the file were left unchanged.");
        }catch(Exception ex){ra.addFlashAttribute("errorMessage","Import was not completed: "+message(ex));}
        return "redirect:/admin/master-data";
    }

    @ModelAttribute("recentImports") public List<MasterDataImportLog> recentImports(){return logs.findTop10ByOrderByImportedAtDesc();}

    private static int depth(String group,Map<String,MasterDataGroup> all){int d=0;Set<String> seen=new HashSet<>();MasterDataGroup g=all.get(group);while(g!=null&&g.getParentGroupKey()!=null&&!g.getParentGroupKey().isBlank()&&seen.add(g.getGroupKey())){d++;g=all.get(g.getParentGroupKey());}return d;}
    private static String key(String g,String c){return g+"\u0000"+c;}
    private static String clean(String s){return s==null?"":s.trim();}
    private static String safeName(String s){if(s==null||s.isBlank())return "master-data.csv";return s.replaceAll("[^A-Za-z0-9._-]","_");}
    private static String message(Exception e){return e.getMessage()==null?"Please check the CSV and try again.":e.getMessage();}
    private static String csv(String s){String v=s==null?"":s;return "\""+v.replace("\"","\"\"")+"\"";}

    private static List<List<String>> parseCsv(String input){
        String text=input==null?"":input.replace("\uFEFF","");List<List<String>> rows=new ArrayList<>();List<String> row=new ArrayList<>();StringBuilder cell=new StringBuilder();boolean quoted=false;
        for(int i=0;i<text.length();i++){char ch=text.charAt(i);if(quoted){if(ch=='"'){if(i+1<text.length()&&text.charAt(i+1)=='"'){cell.append('"');i++;}else quoted=false;}else cell.append(ch);}else{if(ch=='"'&&cell.length()==0)quoted=true;else if(ch==','){row.add(cell.toString());cell.setLength(0);}else if(ch=='\n'||ch=='\r'){if(ch=='\r'&&i+1<text.length()&&text.charAt(i+1)=='\n')i++;row.add(cell.toString());cell.setLength(0);if(!(row.size()==1&&row.get(0).isEmpty()))rows.add(row);row=new ArrayList<>();}else cell.append(ch);}}
        if(quoted)throw new IllegalArgumentException("CSV contains an unclosed quoted field.");if(cell.length()>0||!row.isEmpty()){row.add(cell.toString());rows.add(row);}return rows;
    }
    private record ImportRow(int line,String groupKey,String code,String label,String parentCode,boolean active,int sortOrder,boolean update){}
}
