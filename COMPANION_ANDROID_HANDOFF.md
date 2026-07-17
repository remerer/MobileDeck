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

### Task 0 verification evidence

- PC `npm test -- --run`: passed, 1 file and 19 tests.
- PC `npm run build`: passed, `tsc && vite build`, 44 modules transformed.
- Android `.\gradlew.bat :app:testDebugUnitTest --tests com.remerer.mobiledeck.CompanionButtonContractTest`: passed.
- Android `.\gradlew.bat :app:compileDebugAndroidTestKotlin`: passed.
- Native PC runtime, Android device import/export, and cross-app execution remain for later Integration QA.

<!-- MD-COMP-018 START -->
## MD-COMP-018: Guarded Codex Submit And Transient Android Status

- Origin: `MobileDeckCompanion`
- Direction: `pc_to_android_sync`
- Reviewed PC range: `619823890f8eda1cc42ba605aeb5a7d67f8569a5..62e6c11638f0296478765fd1b97be90cfa0b0c5b`
- Contract impact: `required`
- Contract areas: `interface`, `design`, `settings`
- Counterpart required: `true`
- Schema migration: `none`
- Deck bundle version: `2`
- Android next stage: `android_impact_decision` followed by Android Tasks 6-7
- PC contract sources to inspect:
  - `../MobileDeckCompanion/src-tauri/src/integrations.rs`
  - `../MobileDeckCompanion/src-tauri/src/protocol.rs`
  - `../MobileDeckCompanion/src-tauri/src/codex_jobs.rs`
  - `../MobileDeckCompanion/src-tauri/src/codex_process.rs`

### Persisted button payload

The Android button continues to persist the Codex binding only in the existing `DeckButton.payload` string. Do not add a `DeckButton` field, change the button codec, or increment the bundle version.

```json
{
  "programId": "codex",
  "command": "exec.submit",
  "args": {
    "contractVersion": 1,
    "presetId": "<preset-id>",
    "bindingId": "<binding-id>"
  }
}
```

The payload root keys and `args` keys are exact. Reject missing, extra, mistyped, blank, or unknown-version fields in the release Codex path. Prompt, model, sandbox, workspace path, approval, timeout, and preset/binding content remain PC-owned and must never be accepted from the button.

### Frozen remote request DTOs

All examples below use placeholders only. `deviceId` and `deviceName` are display/correlation metadata; they are not authorization principals.

`exec.submit`:

```json
{
  "type": "program.command",
  "requestId": "<request-id>",
  "pairingToken": "<pairing-token>",
  "deviceId": "<display-device-id>",
  "deviceName": "<display-device-name>",
  "programId": "codex",
  "command": "exec.submit",
  "args": {
    "contractVersion": 1,
    "presetId": "<preset-id>",
    "bindingId": "<binding-id>"
  }
}
```

`exec.status`:

```json
{
  "type": "program.command",
  "requestId": "<request-id>",
  "pairingToken": "<pairing-token>",
  "deviceId": "<display-device-id>",
  "deviceName": "<display-device-name>",
  "programId": "codex",
  "command": "exec.status",
  "args": {
    "contractVersion": 1,
    "jobId": "<job-id>",
    "bindingId": "<binding-id>"
  }
}
```

`exec.cancel` is part of the PC remote contract but is not allowed from Android release phase 1:

```json
{
  "type": "program.command",
  "requestId": "<request-id>",
  "pairingToken": "<pairing-token>",
  "deviceId": "<display-device-id>",
  "deviceName": "<display-device-name>",
  "programId": "codex",
  "command": "exec.cancel",
  "args": {
    "contractVersion": 1,
    "jobId": "<job-id>"
  }
}
```

The PC rejects unknown fields inside each command's `args`. Android must construct submit/status requests from typed values and must not forward an arbitrary `program.command` object.

### Frozen response DTO and redaction

Every successful submit, status, or cancel response uses the existing Companion envelope and the same exact job projection:

```json
{
  "requestId": "<request-id>",
  "ok": true,
  "errorCode": null,
  "message": "Codex command handled",
  "data": {
    "contractVersion": 1,
    "jobId": "<job-id>",
    "bindingId": "<binding-id>",
    "presetId": "<preset-id>",
    "status": "queued",
    "duplicate": false,
    "acceptedAt": "<rfc3339-timestamp>",
    "updatedAt": "<rfc3339-timestamp>",
    "startedAt": null,
    "finishedAt": null,
    "elapsedMs": 0,
    "cancelRequested": false,
    "errorCode": null,
    "message": "Queued",
    "summary": null
  }
}
```

- `status` is exactly one of `queued`, `running`, `completed`, `failed`, `cancelled`.
- `startedAt` and `finishedAt` are nullable RFC 3339 timestamps; `elapsedMs` is a non-negative integer.
- Fixed job messages are `Queued`, `Running`, `Cancellation requested`, `Completed`, `Codex job failed`, and `Cancelled` according to state.
- `summary` is always `null` remotely. Android must reject or discard any non-null summary and must not accept/render/store raw output, agent text, prompt, canonical path, workspace label/path, token, journal data, thread id, stdout, or stderr.
- A command-level error uses the same envelope with `ok:false`, `data:{}`, and a safe `errorCode`/message. Do not render arbitrary response text as a job detail.

The exact stable job error-code allowlist is:

```text
invalid_contract_version
invalid_request
binding_not_found
preset_not_found
binding_preset_mismatch
execution_disabled
execution_mode_insufficient
workspace_not_allowlisted
workspace_mismatch
workspace_unavailable
workspace_write_expired
job_not_found
job_not_active
codex_cli_unavailable
codex_auth_required
approval_required
spawn_failed
output_parse_failed
exec_failed
timed_out
cancel_failed
companion_restarted
journal_io_failed
internal_error
```

An outer transport authentication failure such as `unauthorized` is not a `CodexJobSnapshot.errorCode`; treat it as disconnected/reconnecting rather than a terminal job code.

### Ownership and duplicate behavior

- Pairing authentication creates the private PC principal. Client-provided display device fields do not establish ownership.
- Submit ownership is the authenticated principal plus `bindingId`; the binding must exist and match `presetId`.
- Status requires the same authenticated principal and the requested `bindingId`. Ownership or binding mismatch returns `job_not_found`.
- Remote cancel is owner-scoped on PC, but Android release must not expose or send it.
- A duplicate submit while the same principal and binding already has a queued/running job returns the same `jobId`, current active status, and `duplicate:true`; it does not launch a second process. The duplicate marker is a submit-response indicator, not persisted history.
- Android must suppress a second local submit while its button state is queued/running. If local state was lost, accept the PC duplicate response and reconstruct the active state from the returned job.

### Android timing and transient state

- Poll an active job with `exec.status` every `750 ms`.
- On transport loss, retain the last known job state, show reconnecting, and retry after `1 s`, then `2 s`, then `4 s`; continue retrying every `4 s` until connected.
- Resume `750 ms` status polling after reconnect.
- Clear `completed` and `cancelled` display state after `5 s` without changing the persisted button. Keep `failed` until the next tap or explicit detail dismissal.
- The existing Companion request timeout remains `2200 ms`.

### Android release policy

Release may send only:

1. Existing `status.ping`.
2. `program.command` with `programId:"codex"`, `command:"exec.submit"`, and the exact parsed binding payload above.
3. `program.command` with `programId:"codex"`, `command:"exec.status"`, constructed from the accepted job and binding.

These routes require `CompanionSettings.enabled == true`, a nonblank endpoint and pairing credential, and successful strict contract parsing. Release must locally reject OBS, generic `program.command`, `exec.cancel`, `status.check`, `actions.list`, raw prompt text, and all model/sandbox/workspace/approval/timeout or other override fields. Existing debug-only Companion development routes, deck sync automation, diagnostics, test buttons, OBS controls, and developer controls remain debug-only.

### UI acceptance

- Keep status inside the existing button bounds with no layout-size change and no persisted task state.
- Show `Queued`, `Running`, `Completed`, `Failed`, and `Cancelled` transient states.
- Show elapsed time for running; never show a fabricated percentage.
- Failed state shows only an allowlisted safe code.
- Show disconnected/reconnecting without erasing the last known job state.
- Disable/suppress a second submit while the button is active.
- PC execution starts `disabled` on every Companion launch. Read-only and acknowledged 15-minute workspace-write arming remain PC-only; Android receives only the resulting safe status/error.

### Runtime prerequisite and residual

The reviewed PC resolver/security change is approved and the prior access-denied launch failure is fixed. The currently installed npm `codex-cli 0.142.0` still rejects configured model `gpt-5.6-sol`. The current PC contract reports this as terminal `failed` with existing safe code `exec_failed`; Android must display that code and must not invent `codex_cli_incompatible` or downgrade the model.

Live WebSocket/Android round trip, device rendering, reconnect behavior, and release-policy enforcement remain unverified until Android implementation and Integration QA.

### Android done and block conditions

Done requires typed contract tests, strict release-route tests, transient UI tests, no second active submit, exact timing literals, bundle version `2`, no new persisted button field, and reconciliation against the reviewed PC projection.

Block Android/reconciliation if any DTO field or enum differs, unknown/raw data crosses the Android boundary, release can reach a rejected route, a duplicate starts another job, timing differs, product code persists task state, bundle version changes, a new public error code is proposed without contract review, or the external CLI/model prerequisite is misreported as success.
<!-- MD-COMP-018 END -->
