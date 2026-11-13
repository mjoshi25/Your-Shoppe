# Phase 23 Map Picker Fix

## Included
- Interactive Google Map is rendered in customer and vendor account settings.
- Interactive Google Map is rendered in customer checkout.
- Google Places autocomplete is embedded above each map.
- Selecting a place fills formatted address and available city/state/PIN fields.
- A draggable marker and map click allow precise coordinate adjustment.
- Customer and vendor latitude/longitude are persisted in MongoDB profile documents.
- Checkout captures delivery coordinates and sends those coordinates to Google Routes API for driving distance.
- Orders retain the selected delivery latitude/longitude and delivery pricing snapshot.
- Distance quotes are fingerprinted with selected coordinates as well as address fields.

## Required Google Cloud APIs
- Maps JavaScript API
- Places API (New)
- Routes API

## Render environment variables
- GOOGLE_MAPS_BROWSER_API_KEY: browser key restricted to the production website referrers and Maps JavaScript API + Places API (New).
- GOOGLE_MAPS_API_KEY: server key restricted to Routes API.

## Vendor setup requirement
Each vendor using distance-based delivery must open Settings, search for the exact shop address, select a suggestion, adjust the pin if necessary, and save the profile. Existing vendors must do this once to populate shop coordinates.

## Customer checkout
For distance-based delivery, the customer must select a location from Places search or click/drag the map marker before calculating a delivery quote. PIN-code serviceability validation remains active.

## Validation
ZIP integrity and source-level static checks are performed. Maven compilation, live Google API calls, and browser testing must be completed in the user's environment before production release.
