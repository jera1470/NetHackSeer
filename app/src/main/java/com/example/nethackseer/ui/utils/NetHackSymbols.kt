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
        "BLACK" -> Color(0xFF555555) // Dark gray for visibility
        "RED", "CLR_RED" -> Color(0xFFFF0000)
        "GREEN", "CLR_GREEN" -> Color(0xFF008000)
        "BROWN", "CLR_BROWN" -> Color(0xFF8B4513)
        "BLUE", "CLR_BLUE" -> Color(0xFF0000FF)
        "MAGENTA", "CLR_MAGENTA" -> Color(0xFFFF00FF)
        "CYAN", "CLR_CYAN" -> Color(0xFF00FFFF)
        "GRAY", "CLR_GRAY" -> Color(0xFF808080)
        "NO_COLOR" -> Color(0xFFA9A9A9)
        "ORANGE", "CLR_ORANGE" -> Color(0xFFFFA500)
        "BRIGHT_GREEN", "CLR_BRIGHT_GREEN" -> Color(0xFF00FF00)
        "YELLOW", "CLR_YELLOW" -> Color(0xFFFFFF00)
        "BRIGHT_BLUE", "CLR_BRIGHT_BLUE" -> Color(0xFF0000FF)
        "BRIGHT_MAGENTA", "CLR_BRIGHT_MAGENTA" -> Color(0xFFFF00FF)
        "BRIGHT_CYAN", "CLR_BRIGHT_CYAN" -> Color(0xFF00FFFF)
        "WHITE", "CLR_WHITE" -> Color(0xFFFFFFFF)
        
        // NetHack specific color macros
        "HI_OBJ" -> Color(0xFFFF00FF)         // CLR_MAGENTA
        "HI_METAL" -> Color(0xFF00FFFF)       // CLR_CYAN
        "HI_COPPER" -> Color(0xFFFFFF00)      // CLR_YELLOW
        "HI_SILVER" -> Color(0xFF808080)      // CLR_GRAY
        "HI_GOLD" -> Color(0xFFFFFF00)        // CLR_YELLOW
        "HI_LEATHER" -> Color(0xFF8B4513)     // CLR_BROWN
        "HI_CLOTH" -> Color(0xFF8B4513)       // CLR_BROWN
        "HI_ORGANIC" -> Color(0xFF8B4513)     // CLR_BROWN
        "HI_WOOD" -> Color(0xFF8B4513)        // CLR_BROWN
        "HI_PAPER" -> Color(0xFFFFFFFF)       // CLR_WHITE
        "HI_GLASS" -> Color(0xFF00FFFF)       // CLR_BRIGHT_CYAN
        "HI_MINERAL" -> Color(0xFF808080)     // CLR_GRAY
        "DRAGON_SILVER" -> Color(0xFF00FFFF)  // CLR_BRIGHT_CYAN
        "HI_ZAP" -> Color(0xFF0000FF)         // CLR_BRIGHT_BLUE
        "HI_DOMESTIC" -> Color(0xFFFFFFFF)    // CLR_WHITE
        "HI_LORD" -> Color(0xFFFF00FF)        // CLR_MAGENTA
        
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
