package com.example.notes.model

class Card (
    var description: String,
    var title: String?,
    var type: CardType,
    var id: Long
    ) {
    enum class CardType {
        FOLDER, NOTE
    }
}