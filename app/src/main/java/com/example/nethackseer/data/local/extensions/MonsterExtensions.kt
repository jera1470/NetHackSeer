package com.example.nethackseer.data.local.extensions

import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.MonsterEntity

// helper functions
fun MonsterEntity.hasM1(flag: String): Boolean =
    m1Flags.split("|").any { it.trim() == flag }

fun MonsterEntity.hasM2(flag: String): Boolean =
    m2Flags.split("|").any { it.trim() == flag }

fun MonsterEntity.hasM3(flag: String): Boolean =
    m3Flags.split("|").any { it.trim() == flag }

fun MonsterEntity.hasGeno(flag: String): Boolean =
    genoFlags.split("|").any { it.trim() == flag }

fun MonsterEntity.hasResistance(typ: String): Boolean =
    resistances.split("|").any { it.trim() == typ }

fun MonsterEntity.hasConferred(typ: String): Boolean =
    resistancesConferred.split("|").any { it.trim() == typ }

fun MonsterEntity.matchesName(vararg candidates: String): Boolean {
    val lower = name.lowercase()
    return candidates.any { lower == it.lowercase() }
}

val MonsterEntity.allAttacks: List<Attack>
    get() = listOf(attack1, attack2, attack3, attack4, attack5, attack6)

val MonsterEntity.activeAttacks: List<Attack>
    get() = allAttacks.filter { it.type != "NO_ATTK" }

fun MonsterEntity.hasAttackType(aatyp: String): Boolean =
    activeAttacks.any { it.type == aatyp }

fun MonsterEntity.hasDamageType(adtyp: String): Boolean =
    activeAttacks.any { it.damageType == adtyp }

// macros from mondata.h
val MonsterEntity.isVerySmall: Boolean
    get() = size == "MZ_TINY"

val MonsterEntity.isBigMonster: Boolean
    get() = size == "MZ_LARGE" || size == "MZ_HUGE" || size == "MZ_GIGANTIC"

fun MonsterEntity.pmResistance(typ: String): Boolean =
    hasResistance(typ)

val MonsterEntity.isImmunePoisonGas: Boolean
    get() = matchesName("hezrou", "vrock")

val MonsterEntity.isFlyer: Boolean
    get() = hasM1("M1_FLY")

val MonsterEntity.isFloater: Boolean
    get() = symbol == "S_EYE" || symbol == "S_LIGHT"

val MonsterEntity.isClinger: Boolean
    get() = hasM1("M1_CLING")

val MonsterEntity.isGrounded: Boolean
    get() = !isFlyer && !isFloater && !isClinger

val MonsterEntity.isSwimmer: Boolean
    get() = hasM1("M1_SWIM")

val MonsterEntity.isBreathless: Boolean
    get() = hasM1("M1_BREATHLESS")

val MonsterEntity.isAmphibious: Boolean
    get() = hasM1("M1_AMPHIBIOUS")

val MonsterEntity.cantDrown: Boolean
    get() = isSwimmer || isAmphibious || isBreathless

val MonsterEntity.passesWalls: Boolean
    get() = hasM1("M1_WALLWALK")

val MonsterEntity.isAmorphous: Boolean
    get() = hasM1("M1_AMORPHOUS")

val MonsterEntity.isNoncorporeal: Boolean
    get() = symbol == "S_GHOST"

val MonsterEntity.tunnels: Boolean
    get() = hasM1("M1_TUNNEL")

val MonsterEntity.needsPick: Boolean
    get() = hasM1("M1_NEEDPICK")

val MonsterEntity.hidesUnder: Boolean
    get() = hasM1("M1_CONCEAL")

val MonsterEntity.isHider: Boolean
    get() = hasM1("M1_HIDE")

val MonsterEntity.isCeilingHider: Boolean
    get() = isHider && ((isClinger && symbol != "S_MIMIC") || isFlyer)

val MonsterEntity.hasEyes: Boolean
    get() = !hasM1("M1_NOEYES")

val MonsterEntity.eyeCount: Int
    get() = when {
        !hasEyes -> 0
        matchesName("cyclops", "floating eye") -> 1
        else -> 2
    }

val MonsterEntity.noHands: Boolean
    get() = hasM1("M1_NOHANDS")

val MonsterEntity.noLimbs: Boolean
    get() = hasM1("M1_NOLIMBS")

val MonsterEntity.noTake: Boolean
    get() = hasM1("M1_NOTAKE")

val MonsterEntity.hasHead: Boolean
    get() = !hasM1("M1_NOHEAD")

val MonsterEntity.hasHorns: Boolean
    get() = matchesName("horned devil", "balrog", "minotaur", "asmodeus", "baalzebub")

val MonsterEntity.isWhirly: Boolean
    get() = symbol == "S_VORTEX" || matchesName("air elemental")

val MonsterEntity.isFlaming: Boolean
    get() = matchesName("fire vortex", "flaming sphere", "fire elemental", "salamander")

val MonsterEntity.isSilent: Boolean
    get() = sound == "MS_SILENT"

val MonsterEntity.isUnsolid: Boolean
    get() = hasM1("M1_UNSOLID")

val MonsterEntity.isMindless: Boolean
    get() = hasM1("M1_MINDLESS")

val MonsterEntity.isHumanoid: Boolean
    get() = hasM1("M1_HUMANOID")

val MonsterEntity.isAnimal: Boolean
    get() = hasM1("M1_ANIMAL")

val MonsterEntity.isSlithy: Boolean
    get() = hasM1("M1_SLITHY")

val MonsterEntity.isWooden: Boolean
    get() = matchesName("wood golem")

val MonsterEntity.isThickSkinned: Boolean
    get() = hasM1("M1_THICK_HIDE")

val MonsterEntity.hugThrottles: Boolean
    get() = matchesName("rope golem")

val MonsterEntity.digests: Boolean
    get() = activeAttacks.any { it.damageType == "AD_DGST" && it.type == "AT_ENGL" }

val MonsterEntity.enfolds: Boolean
    get() = activeAttacks.any { it.damageType == "AD_WRAP" && it.type == "AT_ENGL" }

val MonsterEntity.isSlimeproof: Boolean
    get() = matchesName("green slime") || isFlaming || isNoncorporeal

val MonsterEntity.laysEggs: Boolean
    get() = hasM1("M1_OVIPAROUS")

val MonsterEntity.eggsInWater: Boolean
    get() = laysEggs && symbol == "S_EEL" && isSwimmer

val MonsterEntity.regenerates: Boolean
    get() = hasM1("M1_REGEN")

val MonsterEntity.perceives: Boolean
    get() = hasM1("M1_SEE_INVIS")

val MonsterEntity.canTeleport: Boolean
    get() = hasM1("M1_TPORT")

val MonsterEntity.controlTeleport: Boolean
    get() = hasM1("M1_TPORT_CNTRL")

val MonsterEntity.isTelepathic: Boolean
    get() = matchesName("floating eye", "mind flayer", "master mind flayer")

val MonsterEntity.isArmed: Boolean
    get() = hasAttackType("AT_WEAP")

val MonsterEntity.isAcidic: Boolean
    get() = hasM1("M1_ACID")

val MonsterEntity.isPoisonous: Boolean
    get() = hasM1("M1_POIS")

val MonsterEntity.isCarnivorous: Boolean
    get() = hasM1("M1_CARNIVORE")

val MonsterEntity.isHerbivorous: Boolean
    get() = hasM1("M1_HERBIVORE")

val MonsterEntity.isMetallivorous: Boolean
    get() = hasM1("M1_METALLIVORE")

val MonsterEntity.isInediate: Boolean
    get() = !isCarnivorous && !isHerbivorous && !hasM1("M1_OMNIVORE")

val MonsterEntity.isPolyOk: Boolean
    get() = !hasM2("M2_NOPOLY")

val MonsterEntity.isShapeshifter: Boolean
    get() = hasM2("M2_SHAPESHIFTER")

val MonsterEntity.isUndead: Boolean
    get() = hasM2("M2_UNDEAD")

val MonsterEntity.isWere: Boolean
    get() = hasM2("M2_WERE")

val MonsterEntity.isElf: Boolean
    get() = hasM2("M2_ELF")

val MonsterEntity.isDwarf: Boolean
    get() = hasM2("M2_DWARF")

val MonsterEntity.isGnome: Boolean
    get() = hasM2("M2_GNOME")

val MonsterEntity.isOrc: Boolean
    get() = hasM2("M2_ORC")

val MonsterEntity.isHuman: Boolean
    get() = hasM2("M2_HUMAN")

val MonsterEntity.isBat: Boolean
    get() = matchesName("bat", "giant bat", "vampire bat")

val MonsterEntity.isBird: Boolean
    get() = symbol == "S_BAT" && !isBat

val MonsterEntity.isGiant: Boolean
    get() = hasM2("M2_GIANT")

val MonsterEntity.isGolem: Boolean
    get() = symbol == "S_GOLEM"

val MonsterEntity.isDomestic: Boolean
    get() = hasM2("M2_DOMESTIC")

val MonsterEntity.isDemon: Boolean
    get() = hasM2("M2_DEMON")

val MonsterEntity.isMercenary: Boolean
    get() = hasM2("M2_MERC")

val MonsterEntity.isMale: Boolean
    get() = hasM2("M2_MALE")

val MonsterEntity.isFemale: Boolean
    get() = hasM2("M2_FEMALE")

val MonsterEntity.isNeuter: Boolean
    get() = hasM2("M2_NEUTER")

val MonsterEntity.isWanderer: Boolean
    get() = hasM2("M2_WANDER")

val MonsterEntity.isAlwaysHostile: Boolean
    get() = hasM2("M2_HOSTILE")

val MonsterEntity.isAlwaysPeaceful: Boolean
    get() = hasM2("M2_PEACEFUL")

val MonsterEntity.isExtraNasty: Boolean
    get() = hasM2("M2_NASTY")

val MonsterEntity.isStrongMonster: Boolean
    get() = hasM2("M2_STRONG")

val MonsterEntity.canBreathe: Boolean
    get() = hasAttackType("AT_BREA")

val MonsterEntity.cantWield: Boolean
    get() = noHands || isVerySmall

val MonsterEntity.couldTwoWeapon: Boolean
    get() = activeAttacks.take(3).count { it.type == "AT_WEAP" } > 1

val MonsterEntity.throwsRocks: Boolean
    get() = hasM2("M2_ROCKTHROW")

val MonsterEntity.typeIsPname: Boolean
    get() = hasM2("M2_PNAME")

val MonsterEntity.isLord: Boolean
    get() = hasM2("M2_LORD")

val MonsterEntity.isPrince: Boolean
    get() = hasM2("M2_PRINCE")

val MonsterEntity.isNdemon: Boolean
    get() = isDemon && !isLord && !isPrince

val MonsterEntity.isDlord: Boolean
    get() = isDemon && isLord

val MonsterEntity.isDprince: Boolean
    get() = isDemon && isPrince

val MonsterEntity.isMinion: Boolean
    get() = hasM2("M2_MINION")

val MonsterEntity.likesGold: Boolean
    get() = hasM2("M2_GREEDY")

val MonsterEntity.likesGems: Boolean
    get() = hasM2("M2_JEWELS")

val MonsterEntity.likesObjs: Boolean
    get() = hasM2("M2_COLLECT") || isArmed

val MonsterEntity.likesMagic: Boolean
    get() = hasM2("M2_MAGIC")

val MonsterEntity.isWebmaker: Boolean
    get() = matchesName("cave spider", "giant spider")

val MonsterEntity.isUnicorn: Boolean
    get() = symbol == "S_UNICORN" && likesGems

val MonsterEntity.isLongworm: Boolean
    get() = matchesName("baby long worm", "long worm", "long worm tail")

val MonsterEntity.isCovetous: Boolean
    get() = hasM3("M3_COVETOUS")

val MonsterEntity.hasInfravision: Boolean
    get() = hasM3("M3_INFRAVISION")

val MonsterEntity.isInfravisible: Boolean
    get() = hasM3("M3_INFRAVISIBLE")

val MonsterEntity.isDisplacer: Boolean
    get() = hasM3("M3_DISPLACES")

val MonsterEntity.isWatch: Boolean
    get() = matchesName("watchman", "watch captain")

val MonsterEntity.isRider: Boolean
    get() = matchesName("death", "famine", "pestilence")

val MonsterEntity.isPlaceholder: Boolean
    get() = matchesName("orc", "giant", "elf", "human")

val MonsterEntity.isReviver: Boolean
    get() = isRider || symbol == "S_TROLL"

val MonsterEntity.isUniqueCorpstat: Boolean
    get() = hasGeno("G_UNIQ")

val MonsterEntity.emitsLight: Int
    get() = if (symbol == "S_LIGHT" || matchesName(
            "flaming sphere",
            "shocking sphere",
            "baby gold dragon",
            "fire vortex",
            "fire elemental",
            "gold dragon"
        )
    ) 1 else 0

val MonsterEntity.likesLava: Boolean
    get() = matchesName("fire elemental", "salamander")

val MonsterEntity.isPmInvisible: Boolean
    get() = matchesName("stalker", "black light")

val MonsterEntity.likesFire: Boolean
    get() = matchesName("fire vortex", "flaming sphere") || likesLava

val MonsterEntity.touchPetrifies: Boolean
    get() = matchesName("cockatrice", "chickatrice")

val MonsterEntity.fleshPetrifies: Boolean
    get() = touchPetrifies || matchesName("medusa")

val MonsterEntity.passesRocks: Boolean
    get() = passesWalls && !isUnsolid

val MonsterEntity.isMindFlayer: Boolean
    get() = matchesName("mind flayer", "master mind flayer")

val MonsterEntity.isVampire: Boolean
    get() = symbol == "S_VAMPIRE"

val MonsterEntity.hatesLight: Boolean
    get() = matchesName("gremlin")

val MonsterEntity.isWeirdNonliving: Boolean
    get() = isGolem || symbol == "S_VORTEX"

val MonsterEntity.isNonliving: Boolean
    get() = isUndead || matchesName("manes") || isWeirdNonliving

val MonsterEntity.completelyBurns: Boolean
    get() = matchesName("paper golem", "straw golem")

val MonsterEntity.completelyRots: Boolean
    get() = matchesName("wood golem", "leather golem")

val MonsterEntity.completelyRusts: Boolean
    get() = matchesName("iron golem")

val MonsterEntity.isVegan: Boolean
    get() = symbol in listOf("S_BLOB", "S_JELLY", "S_FUNGUS", "S_VORTEX", "S_LIGHT") ||
            (symbol == "S_ELEMENTAL" && !matchesName("stalker")) ||
            (symbol == "S_GOLEM" && !matchesName("flesh golem", "leather golem")) ||
            isNoncorporeal

val MonsterEntity.isVegetarian: Boolean
    get() = isVegan || (symbol == "S_PUDDING" && !matchesName("black pudding"))

val MonsterEntity.isCorpseEater: Boolean
    get() = matchesName("purple worm", "baby purple worm", "ghoul", "piranha")

val MonsterEntity.isDomesticCarnivore: Boolean
    get() = (isDomestic && isCarnivorous) || matchesName(
        "kitten", "housecat", "large cat", "little dog", "dog", "large dog"
    )

/**
 * MonsterData singleton and eating effect helpers.
 */
object MonsterData {

    /**
     * Determines special effects when consuming a monster's corpse.
     */
    fun getEatingSpecialEffects(monster: MonsterEntity, baseConferred: List<String>): List<String> {
        val effects = mutableListOf<String>()
        val lowerName = monster.name.lowercase()

        if (monster.fleshPetrifies) {
            effects.add("Instant petrification")
        }

        if (monster.isRider) {
            effects.add("Instantly fatal")
        }

        if (monster.matchesName("green slime")) {
            effects.add("Causes sliming (10 turns)")
        }

        val isAcidic = baseConferred.contains("MR_ACID") || monster.isAcidic
        if (monster.matchesName("lizard") || isAcidic) {
            if (monster.matchesName("lizard")) {
                effects.add("Reduce stun/confusion to 2 turns")
            }
            effects.add("Cures petrification")
        }

        if (monster.isDomesticCarnivore) {
            effects.add("Aggravates monsters")
        }

        if (monster.matchesName("wraith")) effects.add("Gain level")
        if (monster.isWere) effects.add("Contract lycanthropy")

        when (lowerName) {
            "small mimic" -> effects.add("Mimic an object (20 turns)")
            "large mimic" -> effects.add("Mimic an object (40 turns)")
            "giant mimic" -> effects.add("Mimic an object (50 turns)")
        }

        if (monster.isShapeshifter) effects.add("Polymorph")
        if (monster.matchesName("nurse")) effects.add("Full heal/cure blindness")
        if (monster.matchesName("stalker")) effects.add("Temp. Invis. (+50-149 turns)\nPerm. Invis./See Invis if already invis.\nStun (+30 turns)")
        if (monster.matchesName("displacer beast")) effects.add("Temp. displacement (+6-36 turns)")
        if (monster.matchesName("yellow light") || monster.isBat) effects.add("Stun (+30 turns)")
        if (monster.matchesName("quantum mechanic")) effects.add("Toggle speed")
        if (monster.matchesName("disenchanter")) effects.add("Lose a random intrinsic")

        val hasHalluAttack = monster.activeAttacks.any { it.damageType == "AD_HALU" || it.damageType == "AD_STUN" }
        if (monster.matchesName("violet fungus") || hasHalluAttack) effects.add("Hallucination (+200 turns)")

        val isMagical = monster.hasAttackType("AT_MAGC")
        if (monster.matchesName("newt") || isMagical) effects.add("Increase energy by 1-3 Pw\n(33% for +1 max Pw if full)")

        return effects
    }

    /**
     * Resolves the list of conferred intrinsic IDs for a monster (including flag-based intrinsics).
     */
    fun getResolvedConferredResistances(monster: MonsterEntity): MutableList<String> {
        val conferredRaw = monster.resistancesConferred
        val baseConferred = if (conferredRaw == "0") {
            mutableListOf()
        } else {
            conferredRaw.split("|").map { it.trim() }.toMutableList()
        }

        if (monster.canTeleport && !baseConferred.contains("MR_TELEPORT")) {
            baseConferred.add("MR_TELEPORT")
        }
        if (monster.controlTeleport && !baseConferred.contains("MR_TELEPORT_CONTROL")) {
            baseConferred.add("MR_TELEPORT_CONTROL")
        }
        if (monster.isTelepathic && !baseConferred.contains("MR_TELEPATHY")) {
            baseConferred.add("MR_TELEPATHY")
        }

        return baseConferred
    }
}
