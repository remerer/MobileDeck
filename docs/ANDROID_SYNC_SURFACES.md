# Android Sync Surfaces

This file lists Android-owned surfaces PC Companion must mirror.

## Design Tokens And Theme Names

- Source: `app/src/main/java/com/remerer/mobiledeck/DeckModels.kt`
- Guide: `design/MOBILEDECK_DESIGN_TOKENS.md`
- Themes: `ClassicDark`, `ClassicLight`, `ConsoleDark`, `ConsoleLight`
- Genesis files are structural references only.

## Button Display Capabilities

- Source: `deckButtonDisplayCapabilities(controlStyle, actionType)` in `DeckModels.kt`
- Normal buttons can show icon/image/title/subtitle.
- `controlStyle != Button` is a dedicated control surface and does not show icon/image/title/subtitle.
- `CompanionControl` normal buttons support text but not icon/image.
- Dedicated control buttons open in the Android `KeyboardInput` edit panel because this panel owns the control style selector.

## Button And Control Visual Rules

- Source: `MainActivity.kt` rendering functions.
- Classic UI keeps per-button colors.
- Console UI uses `consoleButtonDefault` as a single button color.
- Control styles: `Button`, `TrimSlider`, `TrimKnob`, `InfiniteWheel`, `JoyPad`, `AnalogStick`, `CompanionToggle`.

## Companion Control Editing

- Android exposes `CompanionToggle` as a selectable control style.
- Android edits scalar Companion value controls with `source`, `value`, `min`, `max`, `step`, and `unit`.
- `CompanionToggle` edits `source` and boolean `value`; range fields are not meaningful for toggles.
- `AnalogStick` edits `source` and `deadZone`; it sends continuous X/Y values instead of scalar min/max/step values.
- `InfiniteWheel` Companion value controls store `companionControl.kind = "Wheel"`.
- Android top-level button editor panels remain: `AppCommand`, `KeyboardInput`, `Widget`, `RunCommand`, `Utility`.
- PC Companion may use PC-friendly grouping if it writes the same `DeckActionType`, `DeckControlStyle`, payload, and `companionControl` schema.

## Deck Bundle Schema

- Source: `DeckBundleTransfer.kt`, `DeckPersistence.kt`, `DeckModels.kt`
- Current bundle version: 2
- Page button fields:
  - `classicButtons`: Classic source of truth
  - `consoleButtons`: Console source of truth
  - `buttons`: legacy mirror of the exported active mode
- `consoleLayouts.rows` references only IDs from `consoleButtons`.
- Import fallback: if `classicButtons` or `consoleButtons` is missing, Android clones legacy `buttons`.

## Background Media Support

- Source: `DeckBundleTransfer.kt`, `ClassicDeckBackgroundLayer` in `MainActivity.kt`
- Export/import embeds image assets referenced by buttons and Classic background.
- Supported imported asset MIME hints: `image/png`, `image/jpeg`, `image/webp`, `image/gif`, `video/mp4`, `video/webm`.
- Android currently renders still images and GIF background handling where implemented; video may be preserved without full rendering.

## Hotkey And Media-Key Grammar

- Source: key editor and parser logic in `MainActivity.kt`, constants in `DeckPersistence.kt`.
- `+` combines keys, `,` separates sequences.
- Known special keywords render as key chips.
- Quoted strings are literal text; doubled quotes represent a literal quote.
- Media keys are selected from media-key choices and stored as payload constants.

## Companion And Bluetooth State

- Source: `CompanionApiClient.kt`, `HidKeyboardManager.kt`, connection UI in `MainActivity.kt`.
- Companion connected state has priority for sending.
- Bluetooth remains usable independently where Android HID is supported.
- Unsupported Android HID versions force Companion guidance and disable/compact Bluetooth controls.

## Layout Settings

- Classic layout source: columns, rows, spacing, button `position`, `spanColumns`, `spanRows`.
- Console layout source: `consoleLayouts[pageId].rows`, `rowWeights`, shared `sidebarFraction`.
- Console sidebar fraction is global across pages.
- Console row weights are page-specific.

## PC Handoff Rule

When Android changes any surface above, request PC work only through `../MobileDeckCompanion/COMPANION_PC_HANDOFF.md`.

Include screenshots or sample payloads when changing visual behavior or protocol shape.
