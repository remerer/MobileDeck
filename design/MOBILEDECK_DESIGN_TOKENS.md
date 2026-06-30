# MobileDeck Design Tokens

Source of truth: `app/src/main/java/com/remerer/mobiledeck/DeckModels.kt`.

Genesis is a structure reference only. Do not copy Genesis color codes into MobileDeck or PC Companion.

## Theme Groups

### Classic Dark

- app background: `#0F141A`, `#151B22`, `#10151B`
- sidebar: `#10171F` at 94%, border white 8%
- card: `#18212B` at 86%, border white 8%
- text: primary white, secondary white 64%, muted white 38%
- toggle: `#151D26`
- action fill: `#18212B` -> `#131B24`
- neutral icon fill: `#263342`
- console fallback tokens: sidebar `#17212B`, preview `#101820`, button `#24313D`, selected `#245B9D`, system `#1F5DAD`

### Classic Light

- app background: `#F7F9FC`, `#EFF4FA`, `#E6EDF5`
- sidebar: white 95%, border `#D0DAE5`
- card: white 90%, border `#D7E0EA`
- text: primary `#17202A`, secondary `#56616D`, muted `#838E99`
- toggle: `#E8EEF5`
- action fill: `#FFFFFF` -> `#F0F5FA`
- neutral icon fill: `#DDE7F2`
- console fallback tokens: sidebar white 90%, preview `#EFF4FA`, button `#E1E8F0`, selected `#276DB4`, system `#2369B0`

### Console Dark

- app background: `#08111A`, `#112033`, `#0A1825`
- sidebar: `#0A1520` at 92%, border white 8%
- card: `#142638` at 90%, border white 4.5%
- text: primary white, secondary white 64%, muted white 38%
- toggle: `#192B3D`
- action fill: `#132331` -> `#0F1D2B`
- neutral icon fill: `#263A4D`
- preview: `#0E1E2D`
- console button: `#233548`
- selected/active: `#245B9D`
- system/accent: `#1F5DAD`

### Console Light

- app background: `#F6FAFD`, `#ECF3F8`, `#E1ECF3`
- sidebar: `#FAFCFE` at 98%, border `#C9D8E4` at 72%
- card: `#FAFCFE` at 96%, border `#C8D6E3` at 58%
- text: primary `#172A3D`, secondary `#64748A`, muted `#91A0AE`
- toggle: `#F0F5F9`
- action fill: `#F8FCFF` -> `#EAF5FB`
- neutral icon fill: `#E4EEF6`
- preview: `#EEF5FA`
- console button: `#F4F8FB`
- selected/active: `#1976B7`
- system/accent: `#1D82BE`

## Classic Button Colors

Dark palette: `#005A9C`, `#6A4C93`, `#006D77`, `#9D4E15`, `#4F772D`, `#8A1C1C`.

Light palette: `#2F7FC1`, `#8B6BB6`, `#2B9098`, `#C07635`, `#6FA34A`, `#C15353`.

Classic UI keeps per-button background colors.

## Console Button Rules

Console UI uses one button tone per theme. Do not tint console button backgrounds with `DeckButton.color`.

- normal: `consoleButtonDefault`
- selected/pressed/system: `consoleButtonFeatured` or `consoleButtonSystem`
- control accent: console blue accent, not per-button color
- hairline/rim: subtle theme hairline only; no white rim on all buttons

## Status Colors

Use semantic status colors consistently in both Android and PC Companion.

- connected: green/blue connected dot, text primary
- waiting/pairing: blue accent, text secondary
- disconnected: muted text, neutral dot
- error: red accent, destructive controls

## Control Visual Rules

- Slider: orientation follows button aspect ratio. Analog motion stays smooth; emitted trim value can be stepped.
- Limited knob: marker moves; top tick stays fixed at 12 o'clock.
- Infinite wheel: marker moves; wheel ticks stay fixed. One highlighted tick stays at 12 o'clock.
- D-pad: one connected cross shape. Diagonal input means two cardinal directions active at once.
- Analog stick: continuous XY with deadzone. Not tied to Companion availability.
- Toggle: Companion value shown only when Companion reports a value; otherwise it acts as a control button.

## Screenshot References

- `captures/usable_pages/01_classic_deck_page_1.png`
- `captures/usable_pages/04_console_deck_page_1.png`
- `captures/usable_pages/07_classic_settings.png`
- `captures/usable_pages/08_console_settings.png`
- `captures/usable_pages/11_button_study_page_1.png`
- `captures/usable_pages/13_button_study_page_3.png`
