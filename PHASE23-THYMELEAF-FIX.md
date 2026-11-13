# Phase 23 hotfix – settings location autocomplete template parsing

## Fix

The plain JavaScript block in `templates/settings/index.html` contains a JavaScript array literal beginning with `[[`. Thymeleaf interpreted that sequence as its inlined expression syntax and failed to parse the settings page.

The script now declares `th:inline="none"`, so Thymeleaf leaves the JavaScript array literal untouched. The preceding Google Maps script tag retains its Thymeleaf condition and dynamic `th:src` URL.

## Validation

- Confirmed the settings page has the explicit `th:inline="none"` declaration on the location autocomplete script.
- Confirmed the Google Maps script tag still uses its Thymeleaf `th:if` and `th:src` attributes.
- ZIP archive integrity checked after packaging.
- Maven build and live Render test not run in this environment.
