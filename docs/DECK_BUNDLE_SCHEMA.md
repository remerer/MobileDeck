# Deck Bundle Schema

Current format: `mobiledeck.bundle`

Current version: `2`

## Page Shape

```json
{
  "id": 1,
  "name": "Page 1",
  "buttons": [],
  "classicButtons": [],
  "consoleButtons": []
}
```

## Button Ownership

- `classicButtons` is the Classic UI source of truth.
- `consoleButtons` is the Console UI source of truth.
- `buttons` remains for backward compatibility and mirrors the active UI mode during export.
- Android imports old bundles by cloning `buttons` into both `classicButtons` and `consoleButtons`.

## Companion Control

Dedicated Companion value controls store `companionControl` as an object:

```json
{
  "kind": "Slider",
  "source": "system.volume",
  "value": 50,
  "min": 0,
  "max": 100,
  "step": 1,
  "unit": "%"
}
```

Rules:

- `source` identifies the PC-side value or command channel.
- `value` can be number, boolean, or string. `0` is a valid explicit value and is not the same as an omitted value.
- Scalar value controls use `kind`: `Slider`, `Knob`, or `Wheel`.
- Toggle controls use `kind: "Toggle"` and boolean `value`.
- Analog sticks use `kind: "AnalogStick"`, `source`, and `deadZone`; they do not use scalar min/max/step fields.
- D-pads use `kind: "DPad"` when represented as a Companion control.

## Console Layout

```json
{
  "consoleLayouts": {
    "1": {
      "rows": [[101, 102], [103]],
      "rowWeights": [1.0, 1.0],
      "sidebarFraction": 0.22
    }
  }
}
```

Rules:

- `rows` references `consoleButtons` IDs only.
- `rowWeights` is normalized by Android.
- `sidebarFraction` is applied globally across pages by Android.

## Assets

Button images, imported background images, and app icons are embedded in `assets` and referenced as `asset:<id>`.

Import writes embedded assets into Android app-private storage and rewrites the URI for local use.
