# Debug HTML pages

Manual debug UI with a visual floor map renderer.

## Files

- `index.html` - home + shared settings
- `admin.html` - all `AdminController` endpoints
- `map.html` - visual map viewer based on `MapController` data
- `map.js` - rendering logic (SVG layers, zoom, pan, object selection)
- `auth.html` - all `AuthController` endpoints
- `common.js` - shared request helper (base URL + optional bearer)
- `styles.css` - simple styling

## Notes

- Default base URL: `http://localhost:8080/api/v1`
- In `dev` mode, auth may be disabled. Keep bearer empty if not needed.
- Visual mode supports:
  - loading `BuildingDTO` by id and switching floors
  - loading `FloorDTO` directly by id
  - rendering floor contour, rooms, stairs, nodes, and node edges
  - zoom (wheel), pan (drag), fit-to-view and object metadata panel

