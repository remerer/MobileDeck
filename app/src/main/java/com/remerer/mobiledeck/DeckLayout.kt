package com.remerer.mobiledeck

fun slotToButtonPosition(slot: Int, showTitle: Boolean): Int = if (showTitle) slot - 1 else slot

fun buttonToSlot(button: DeckButton, showTitle: Boolean): Int {
    return if (showTitle) button.position + 1 else button.position
}

fun DeckButton.effectiveSpanColumns(columns: Int, showTitle: Boolean): Int {
    val column = buttonToSlot(this, showTitle).floorMod(columns.coerceAtLeast(1))
    return spanColumns.coerceIn(1, minOf(MAX_BUTTON_SPAN_COLUMNS, columns - column).coerceAtLeast(1))
}

fun DeckButton.effectiveSpanRows(columns: Int, rows: Int, showTitle: Boolean): Int {
    val slot = buttonToSlot(this, showTitle)
    val row = slot / columns.coerceAtLeast(1)
    return spanRows.coerceIn(1, minOf(MAX_BUTTON_SPAN_ROWS, rows - row).coerceAtLeast(1))
}

fun occupiedSlotsForButton(
    button: DeckButton,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): List<Int> {
    val safeColumns = columns.coerceAtLeast(1)
    val safeRows = rows.coerceAtLeast(1)
    val anchorSlot = buttonToSlot(button, showTitle)
    if (anchorSlot !in 0 until safeColumns * safeRows) return emptyList()
    val anchorColumn = anchorSlot % safeColumns
    val anchorRow = anchorSlot / safeColumns
    val spanColumns = button.effectiveSpanColumns(safeColumns, showTitle)
    val spanRows = button.effectiveSpanRows(safeColumns, safeRows, showTitle)
    return buildList {
        repeat(spanRows) { rowOffset ->
            repeat(spanColumns) { columnOffset ->
                add((anchorRow + rowOffset) * safeColumns + anchorColumn + columnOffset)
            }
        }
    }
}

fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

fun nextDeckButtonId(buttons: List<DeckButton>): Int {
    return (buttons.maxOfOrNull { it.id } ?: 0) + 1
}

fun nextDeckButtonIdForPages(pages: List<DeckPageConfig>): Int {
    return (pages.flatMap { it.classicButtons + it.consoleButtons }.maxOfOrNull { it.id } ?: 0) + 1
}

fun nextDeckPageId(pages: List<DeckPageConfig>): Int {
    return (pages.maxOfOrNull { it.id } ?: 0) + 1
}

fun nextOpenPosition(buttons: List<DeckButton>, capacity: Int): Int {
    val occupied = buttons.map { it.position }.toSet()
    return (0 until capacity).firstOrNull { it !in occupied } ?: capacity
}

fun nextOpenPosition(
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): Int {
    val capacity = columns * rows - if (showTitle) 1 else 0
    val occupied = buttons.flatMap { occupiedSlotsForButton(it, columns, rows, showTitle) }.toSet()
    return (0 until capacity).firstOrNull { position ->
        val slot = if (showTitle) position + 1 else position
        slot !in occupied
    } ?: capacity
}

fun canPlaceButton(
    button: DeckButton,
    otherButtons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): Boolean {
    val slots = occupiedSlotsForButton(button, columns, rows, showTitle).toSet()
    if (slots.isEmpty() || (showTitle && 0 in slots)) return false
    val otherSlots = otherButtons.flatMap { occupiedSlotsForButton(it, columns, rows, showTitle) }.toSet()
    return slots.intersect(otherSlots).isEmpty()
}

fun buttonAtPosition(
    buttons: List<DeckButton>,
    targetPosition: Int,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): DeckButton? {
    val targetSlot = if (showTitle) targetPosition + 1 else targetPosition
    return buttons.firstOrNull { button ->
        targetSlot in occupiedSlotsForButton(button, columns, rows, showTitle)
    }
}

fun sameButtonSize(
    first: DeckButton,
    second: DeckButton,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): Boolean {
    return first.effectiveSpanColumns(columns, showTitle) == second.effectiveSpanColumns(columns, showTitle) &&
        first.effectiveSpanRows(columns, rows, showTitle) == second.effectiveSpanRows(columns, rows, showTitle)
}

fun shrinkButtonToAvailable(
    button: DeckButton,
    otherButtons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): DeckButton {
    var candidate = button.copy(
        spanColumns = button.spanColumns.coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
        spanRows = button.spanRows.coerceIn(1, MAX_BUTTON_SPAN_ROWS)
    )
    candidate = candidate.copy(
        spanColumns = candidate.effectiveSpanColumns(columns, showTitle),
        spanRows = candidate.effectiveSpanRows(columns, rows, showTitle)
    )
    while (!canPlaceButton(candidate, otherButtons, columns, rows, showTitle) &&
        (candidate.spanColumns > 1 || candidate.spanRows > 1)
    ) {
        candidate = if (candidate.spanColumns >= candidate.spanRows && candidate.spanColumns > 1) {
            candidate.copy(spanColumns = candidate.spanColumns - 1)
        } else {
            candidate.copy(spanRows = candidate.spanRows - 1)
        }
    }
    return candidate
}

fun pageButtonCapacity(pageId: Int, pages: List<DeckPageConfig>, columns: Int, rows: Int): Int {
    val slotCount = columns * rows
    return if (pageId == pages.firstOrNull()?.id) slotCount - 1 else slotCount
}

fun updateDeckPage(
    pages: List<DeckPageConfig>,
    pageId: Int,
    mode: DeckUiMode,
    update: (DeckPageConfig) -> List<DeckButton>
): List<DeckPageConfig> {
    return pages.map { page ->
        if (page.id == pageId) page.withButtonsForMode(mode, update(page)) else page
    }
}

fun updateDeckPage(
    pages: List<DeckPageConfig>,
    pageId: Int,
    update: (DeckPageConfig) -> List<DeckButton>
): List<DeckPageConfig> = updateDeckPage(pages, pageId, DeckUiMode.Classic, update)

fun updateDeckButton(
    pages: List<DeckPageConfig>,
    button: DeckButton,
    mode: DeckUiMode
): List<DeckPageConfig> {
    return pages.map { page ->
        val updated = page.buttonsForMode(mode).map { if (it.id == button.id) button else it }
        page.withButtonsForMode(mode, updated)
    }
}

fun updateDeckButton(
    pages: List<DeckPageConfig>,
    button: DeckButton
): List<DeckPageConfig> = updateDeckButton(pages, button, DeckUiMode.Classic)


