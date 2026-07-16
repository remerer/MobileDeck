# PC Companion to Android Handoff

## MD-SYNC-018: Purpose / Function / Presentation Compatibility

- Origin: `MobileDeckCompanion`
- Direction: `pc_to_android_sync`
- Contract areas: interface, design
- PC implementation sources:
  - `../MobileDeckCompanion/src/buttonEditorModel.ts`
  - `../MobileDeckCompanion/src/buttonEditorModel.test.ts`
  - `../MobileDeckCompanion/src/main.ts`
  - `../MobileDeckCompanion/src/styles.css`
- PC result: `docs/agent_reports/MD-SYNC-018-pc-implementation.md`

### Required Android-side action

At `sync_contract`, verify that Android imports, persists, edits, renders, and executes the existing `AndroidButton` tuples emitted by the new PC editor. No bundle version or field was added. Record which rows already pass unchanged and route only proven gaps to Android implementation.

| PC editor behavior | Persisted AndroidButton tuple | Android verification required |
| --- | --- | --- |
| Execute / Input icon, text, icon + text | `controlStyle=Button` with `displayMode=IconOnly`, `KeywordOnly`, or `IconAndText` | Confirm all three modes preserve `title`, `subtitle`, `icon`, and `iconImageUri` across import/export even when the current purpose hides some metadata. |
| Display CPU | `actionType=CompanionStatus`, `payload=system.cpuUsage`, `controlStyle=Button`, `companionControl.kind=Status` | Confirm read-only status rendering and round-trip persistence. |
| Display memory | `actionType=CompanionStatus`, `payload=system.memoryUsage`, `controlStyle=Button`, `companionControl.kind=Status` | Confirm read-only status rendering and round-trip persistence. |
| Display volume | `actionType=CompanionStatus`, `payload=system.volume`, `controlStyle=Button`, `companionControl.kind=Status` | Confirm volume is rendered as read-only Display, not an editable Control. |
| Volume/custom scalar slider | `actionType=CompanionControl`, `controlStyle=TrimSlider`, `companionControl.kind=Slider` | Confirm Android scalar editor, rendering, and Companion execution use `source`, `value`, `min`, `max`, `step`, and `unit`. |
| Volume/custom scalar knob | `actionType=CompanionControl`, `controlStyle=TrimKnob`, `companionControl.kind=Knob` | Confirm continuous scalar behavior and persistence rather than legacy media-key trim conversion. |
| Volume/custom scalar wheel | `actionType=CompanionControl`, `controlStyle=InfiniteWheel`, `companionControl.kind=Wheel` | Confirm wheel behavior, ranges, and source persistence. |
| Mute/custom toggle | `actionType=CompanionControl`, `controlStyle=CompanionToggle`, `companionControl.kind=Toggle` | Confirm fixed `system.micMute` and custom sources remain distinguishable and execute correctly. |
| Direction pad | `actionType=Hotkey`, `controlStyle=JoyPad`, `payload=UP\|DOWN\|LEFT\|RIGHT` | Confirm existing Android D-pad rendering/execution. |
| Analog position | `actionType=CompanionControl`, `controlStyle=AnalogStick`, `companionControl.kind=AnalogStick` | Confirm source/dead-zone editing and continuous X/Y execution. |

### Contract decisions to record

1. Confirm the PC-friendly four-purpose grouping may remain editor-only as allowed by `docs/ANDROID_SYNC_SURFACES.md`; Android does not need the same top-level grouping unless product direction explicitly requests it.
2. Confirm all shared presentation rows above are already supported by Android. If any row is not, set `counterpart_required: yes` and route only that row to Android implementation.
3. Confirm PC-only functions remain disabled or clearly Companion-dependent on Android and Android-only functions continue to retain their existing mobile execution.
4. Confirm no schema migration or bundle version increment is required.

### PC verification evidence

- `npm test -- --run`: 17 tests passed.
- `npm run build`: TypeScript and Vite production build passed.
- `npm run tauri:build`: Tauri release compile passed with `--no-bundle`.
- Browser UI QA with local mocked Tauri status verified contextual function lists, fixed-presentation hiding, Display read-only behavior, compatible Control alternatives, custom-only source editing, and metadata survival through purpose round trips.
- Native PC runtime, Android device import/export, and cross-app execution remain for later Integration QA.

