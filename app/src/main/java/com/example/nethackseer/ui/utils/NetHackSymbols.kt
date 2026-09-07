package com.example.nethackseer.ui.utils

import androidx.compose.ui.graphics.Color

fun getDisplayChar(symbol: String): String {
    return when (symbol) {
        // Monsters
        "S_ANT" -> "a"
        "S_BLOB" -> "b"
        "S_COCKATRICE" -> "c"
        "S_DOG" -> "d"
        "S_EYE" -> "e"
        "S_FELINE" -> "f"
        "S_GREMLIN" -> "g"
        "S_HUMANOID" -> "h"
        "S_IMP" -> "i"
        "S_JELLY" -> "j"
        "S_KOBOLD" -> "k"
        "S_LEPRECHAUN" -> "l"
        "S_MIMIC" -> "m"
        "S_NYMPH" -> "n"
        "S_ORC" -> "o"
        "S_PIERCER" -> "p"
        "S_QUADRUPED" -> "q"
        "S_RODENT" -> "r"
        "S_SPIDER" -> "s"
        "S_TRAPPER" -> "t"
        "S_UNICORN" -> "u"
        "S_VORTEX" -> "v"
        "S_WORM" -> "w"
        "S_XAN" -> "x"
        "S_LIGHT" -> "y"
        "S_ZRUTY" -> "z"
        "S_ANGEL" -> "A"
        "S_BAT" -> "B"
        "S_CENTAUR" -> "C"
        "S_DRAGON" -> "D"
        "S_ELEMENTAL" -> "E"
        "S_FUNGUS" -> "F"
        "S_GNOME" -> "G"
        "S_GIANT" -> "H"
        "S_JABBERWOCK" -> "J"
        "S_KOP" -> "K"
        "S_LICH" -> "L"
        "S_MUMMY" -> "M"
        "S_NAGA" -> "N"
        "S_OGRE" -> "O"
        "S_PUDDING" -> "P"
        "S_QUANTMECH" -> "Q"
        "S_RUSTMONST" -> "R"
        "S_SNAKE" -> "S"
        "S_TROLL" -> "T"
        "S_UMBER" -> "U"
        "S_VAMPIRE" -> "V"
        "S_WRAITH" -> "W"
        "S_XORN" -> "X"
        "S_YETI" -> "Y"
        "S_ZOMBIE" -> "Z"
        "S_HUMAN" -> "@"
        "S_GHOST" -> " "
        "S_GOLEM" -> "'"
        "S_DEMON" -> "&"
        "S_EEL" -> ";"
        "S_LIZARD" -> ":"
        
        // Items
        "ILLOBJ_CLASS" -> " "
        "WEAPON_CLASS" -> ")"
        "ARMOR_CLASS" -> "["
        "RING_CLASS" -> "="
        "AMULET_CLASS" -> "\""
        "TOOL_CLASS" -> "("
        "FOOD_CLASS" -> "%"
        "POTION_CLASS" -> "!"
        "SCROLL_CLASS" -> "?"
        "SPBOOK_CLASS" -> "+"
        "WAND_CLASS" -> "/"
        "COIN_CLASS" -> "$"
        "GEM_CLASS" -> "*"
        "ROCK_CLASS" -> "`"
        "BALL_CLASS" -> "0"
        "CHAIN_CLASS" -> "_"
        "VENOM_CLASS" -> "."
        
        else -> "?"
    }
}

fun getNetHackColor(color: String): Color {
    return when (color.uppercase()) {
        "BLACK", "CLR_BLACK" -> Color(0xFF333333) // Slightly lighter than pure black for visibility
        "RED", "CLR_RED" -> Color(0xFFAA0000)
        "GREEN", "CLR_GREEN" -> Color(0xFF00AA00)
        "BROWN", "CLR_BROWN" -> Color(0xFFAA5500)
        "BLUE", "CLR_BLUE" -> Color(0xFF0000AA)
        "MAGENTA", "CLR_MAGENTA" -> Color(0xFFAA00AA)
        "CYAN", "CLR_CYAN" -> Color(0xFF00AAAA)
        "GRAY", "CLR_GRAY" -> Color(0xFFAAAAAA)
        "NO_COLOR" -> Color(0xFFAAAAAA)        // Default to Gray cause yeah
        "ORANGE", "CLR_ORANGE" -> Color(0xFFFF5555)
        "BRIGHT_GREEN", "CLR_BRIGHT_GREEN" -> Color(0xFF55FF55)
        "YELLOW", "CLR_YELLOW" -> Color(0xFFFFFF55)
        "BRIGHT_BLUE", "CLR_BRIGHT_BLUE" -> Color(0xFF5555FF)
        "BRIGHT_MAGENTA", "CLR_BRIGHT_MAGENTA" -> Color(0xFFFF55FF)
        "BRIGHT_CYAN", "CLR_BRIGHT_CYAN" -> Color(0xFF55FFFF)
        "WHITE", "CLR_WHITE" -> Color(0xFFFFFFFF)
        "BRIGHT_RED", "CLR_BRIGHT_RED" -> Color(0xFFFF5555)
        
        // NetHack specific color macros from color.h
        "HI_OBJ" -> Color(0xFFAA00AA)         // CLR_MAGENTA
        "HI_METAL" -> Color(0xFF00AAAA)       // CLR_CYAN
        "HI_COPPER" -> Color(0xFFFFFF55)      // CLR_YELLOW
        "HI_SILVER" -> Color(0xFFAAAAAA)      // CLR_GRAY
        "HI_GOLD" -> Color(0xFFFFFF55)        // CLR_YELLOW
        "HI_LEATHER" -> Color(0xFFAA5500)     // CLR_BROWN
        "HI_CLOTH" -> Color(0xFFAA5500)       // CLR_BROWN
        "HI_ORGANIC" -> Color(0xFFAA5500)     // CLR_BROWN
        "HI_WOOD" -> Color(0xFFAA5500)        // CLR_BROWN
        "HI_PAPER" -> Color(0xFFFFFFFF)       // CLR_WHITE
        "HI_GLASS" -> Color(0xFF55FFFF)       // CLR_BRIGHT_CYAN
        "HI_MINERAL" -> Color(0xFFAAAAAA)     // CLR_GRAY
        "DRAGON_SILVER" -> Color(0xFF55FFFF)  // CLR_BRIGHT_CYAN
        "HI_ZAP" -> Color(0xFF5555FF)         // CLR_BRIGHT_BLUE
        "HI_DOMESTIC" -> Color(0xFFFFFFFF)    // CLR_WHITE
        "HI_LORD" -> Color(0xFFAA00AA)        // CLR_MAGENTA
        "HI_OVERLORD" -> Color(0xFFFF55FF)    // CLR_BRIGHT_MAGENTA

        else -> Color.Gray
    }
}

fun cleanNetHackName(name: String): String {
    return name.replace("CLR_YELLOW", "yellow")
        .replace("CLR_WHITE", "white")
        .replace("CLR_RED", "red")
        .replace("CLR_BLUE", "blue")
        .replace("CLR_GREEN", "green")
        .replace("CLR_BROWN", "brown")
        .replace("CLR_MAGENTA", "magenta")
        .replace("CLR_CYAN", "cyan")
        .replace("CLR_GRAY", "gray")
        .replace("CLR_ORANGE", "orange")
        .replace("CLR_BRIGHT_GREEN", "bright green")
        .replace("CLR_BRIGHT_BLUE", "bright blue")
        .replace("CLR_BRIGHT_MAGENTA", "bright magenta")
        .replace("CLR_BRIGHT_CYAN", "bright cyan")
        .replace("HI_METAL", "metal")
        .replace("HI_COPPER", "copper")
        .replace("HI_SILVER", "silver")
        .replace("HI_GOLD", "gold")
}

/**
 * Maps item and monster class symbols to their human-readable names
 */
fun getCategoryDisplayName(symbol: String): String {
    return when (symbol.uppercase()) {
        // Items
        "WEAPON_CLASS" -> "weapon"
        "ARMOR_CLASS" -> "armor"
        "RING_CLASS" -> "ring"
        "AMULET_CLASS" -> "amulet"
        "TOOL_CLASS" -> "tool"
        "FOOD_CLASS" -> "food"
        "POTION_CLASS" -> "potion"
        "SCROLL_CLASS" -> "scroll"
        "SPBOOK_CLASS" -> "spellbook"
        "WAND_CLASS" -> "wand"
        "COIN_CLASS" -> "coin"
        "GEM_CLASS" -> "gem"
        "ROCK_CLASS" -> "rock"
        "BALL_CLASS" -> "iron ball"
        "CHAIN_CLASS" -> "chain"
        "VENOM_CLASS" -> "venom"
        "ILLOBJ_CLASS" -> "illegal object"

        // monsters
        "S_ANT" -> "ant / insect"
        "S_BLOB" -> "blob"
        "S_COCKATRICE" -> "cockatrice"
        "S_DOG" -> "canine"
        "S_EYE" -> "sphere / eye"
        "S_FELINE" -> "feline"
        "S_GREMLIN" -> "gremlin"
        "S_HUMANOID" -> "humanoid"
        "S_IMP" -> "imp"
        "S_JELLY" -> "jelly"
        "S_KOBOLD" -> "kobold"
        "S_LEPRECHAUN" -> "leprechaun"
        "S_MIMIC" -> "mimic"
        "S_NYMPH" -> "nymph"
        "S_ORC" -> "orc"
        "S_PIERCER" -> "piercer"
        "S_QUADRUPED" -> "quadruped"
        "S_RODENT" -> "rodent"
        "S_SPIDER" -> "spider"
        "S_TRAPPER" -> "trapper / lurker above"
        "S_UNICORN" -> "unicorn"
        "S_VORTEX" -> "vortex"
        "S_WORM" -> "worm"
        "S_XAN" -> "xan"
        "S_LIGHT" -> "light"
        "S_ZRUTY" -> "zruty"
        "S_ANGEL" -> "angel"
        "S_BAT" -> "bat"
        "S_CENTAUR" -> "centaur"
        "S_DRAGON" -> "dragon"
        "S_ELEMENTAL" -> "elemental"
        "S_FUNGUS" -> "fungus / mold"
        "S_GNOME" -> "gnome"
        "S_GIANT" -> "giant"
        "S_JABBERWOCK" -> "jabberwock"
        "S_KOP" -> "Keystone Kop"
        "S_LICH" -> "lich"
        "S_MUMMY" -> "mummy"
        "S_NAGA" -> "naga"
        "S_OGRE" -> "ogre"
        "S_PUDDING" -> "pudding"
        "S_QUANTMECH" -> "quantum mechanic or genetic engineer"
        "S_RUSTMONST" -> "rust monster or disenchanter"
        "S_SNAKE" -> "snake"
        "S_TROLL" -> "troll"
        "S_UMBER" -> "umber hulk"
        "S_VAMPIRE" -> "vampire"
        "S_WRAITH" -> "wraith"
        "S_XORN" -> "xorn"
        "S_YETI" -> "yeti"
        "S_ZOMBIE" -> "zombie"
        "S_HUMAN" -> "human"
        "S_GHOST" -> "ghost"
        "S_GOLEM" -> "golem"
        "S_DEMON" -> "demon"
        "S_EEL" -> "sea monster"
        "S_LIZARD" -> "lizard"

        else -> {
            var cleaned = symbol
            if (cleaned.endsWith("_CLASS", ignoreCase = true)) {
                cleaned = cleaned.substring(0, cleaned.length - 6)
            }
            if (cleaned.startsWith("S_", ignoreCase = true)) {
                cleaned = cleaned.substring(2)
            }
            cleaned.lowercase().replace("_", " ")
        }
    }
}
