package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.ArtifactDao
import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ArtifactEntity
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeMonsterDao(initial: List<MonsterEntity> = emptyList()) : MonsterDao {
    private val _entities = MutableStateFlow(initial)

    override suspend fun insertAll(entities: List<MonsterEntity>) {
        _entities.value = entities
    }

    override fun getAll(): Flow<List<MonsterEntity>> = _entities

    override fun getAllNames(): Flow<List<String>> = _entities.map { it.map { m -> m.name }.sorted() }

    override fun search(query: String): Flow<List<MonsterEntity>> = _entities.map { list ->
        val clean = query.replace("%", "").trim().lowercase()
        list.filter { it.name.contains(clean, ignoreCase = true) }
    }

    override fun getMonsterByName(name: String): Flow<MonsterEntity?> = _entities.map { list ->
        list.find { it.name.equals(name, ignoreCase = true) }
    }
}

class FakeItemDao(initial: List<ItemEntity> = emptyList()) : ItemDao {
    private val _entities = MutableStateFlow(initial)

    override suspend fun insertAll(entities: List<ItemEntity>) {
        _entities.value = entities
    }

    override fun getAll(): Flow<List<ItemEntity>> = _entities

    override fun getAllNames(): Flow<List<String>> = _entities.map { it.map { i -> i.name }.sorted() }

    override fun search(query: String): Flow<List<ItemEntity>> = _entities.map { list ->
        val clean = query.replace("%", "").trim().lowercase()
        list.filter { it.name.contains(clean, ignoreCase = true) }
    }

    override fun getItemByName(name: String): Flow<ItemEntity?> = _entities.map { list ->
        list.find { it.name.equals(name, ignoreCase = true) }
    }
}

class FakePropertyDao(initial: List<PropertyEntity> = emptyList()) : PropertyDao {
    private val _entities = MutableStateFlow(initial)

    override suspend fun getPropertyById(id: String): PropertyEntity? =
        _entities.value.find { it.id == id }

    override fun getPropertiesByIds(ids: List<String>): Flow<List<PropertyEntity>> = _entities.map { list ->
        list.filter { it.id in ids }
    }

    override suspend fun insertProperties(properties: List<PropertyEntity>) {
        _entities.value = properties
    }

    override fun getAllProperties(): Flow<List<PropertyEntity>> = _entities

    override fun getPropertyByName(name: String): Flow<PropertyEntity?> = _entities.map { list ->
        list.find { it.name.equals(name, ignoreCase = true) }
    }

    override fun getAllNames(): Flow<List<String>> = _entities.map { it.map { p -> p.name }.sorted() }
}

class FakeArtifactDao(initial: List<ArtifactEntity> = emptyList()) : ArtifactDao {
    private val _entities = MutableStateFlow(initial)

    override suspend fun insertAll(entities: List<ArtifactEntity>) {
        _entities.value = entities
    }

    override fun getAll(): Flow<List<ArtifactEntity>> = _entities

    override fun getAllNames(): Flow<List<String>> = _entities.map { it.map { a -> a.name }.sorted() }

    override fun search(query: String): Flow<List<ArtifactEntity>> = _entities.map { list ->
        val clean = query.replace("%", "").trim().lowercase()
        list.filter { it.name.contains(clean, ignoreCase = true) }
    }

    override fun getArtifactByName(name: String): Flow<ArtifactEntity?> = _entities.map { list ->
        list.find { it.name.equals(name, ignoreCase = true) }
    }
}

object TestEntityFactory {
    fun createAttack(
        type: String = "AT_BITE",
        damageType: String = "AD_PHYS",
        diceCount: Int = 1,
        diceSides: Int = 4
    ) = Attack(
        type = type,
        damageType = damageType,
        diceCount = diceCount,
        diceSides = diceSides
    )

    fun createMonster(
        name: String,
        symbol: String = "S_ANT",
        color: String = "CLR_BROWN",
        level: Int = 2,
        moveRate: Int = 12,
        ac: Int = 3,
        mr: Int = 0,
        alignment: Int = 0,
        resistances: String = "0",
        resistancesConferred: String = "0",
        m1Flags: String = "M1_ANIMAL",
        m2Flags: String = "M2_HOSTILE",
        m3Flags: String = "0",
        genoFlags: String = "0",
        attacks: List<Attack> = listOf(createAttack()),
        weight: Int = 10,
        nutritionValue: Int = 10,
        sound: String = "MS_SILENT",
        size: String = "MZ_MEDIUM",
        difficulty: Int = 4,
        maleName: String? = null,
        femaleName: String? = null
    ): MonsterEntity {
        val emptyAttack = Attack("NO_ATTK", "AD_NONE", 0, 0)
        return MonsterEntity(
            name = name,
            symbol = symbol,
            level = level,
            moveRate = moveRate,
            ac = ac,
            mr = mr,
            alignment = alignment,
            genoFlags = genoFlags,
            attack1 = attacks.getOrElse(0) { emptyAttack },
            attack2 = attacks.getOrElse(1) { emptyAttack },
            attack3 = attacks.getOrElse(2) { emptyAttack },
            attack4 = attacks.getOrElse(3) { emptyAttack },
            attack5 = attacks.getOrElse(4) { emptyAttack },
            attack6 = attacks.getOrElse(5) { emptyAttack },
            weight = weight,
            nutritionValue = nutritionValue,
            sound = sound,
            size = size,
            resistances = resistances,
            resistancesConferred = resistancesConferred,
            m1Flags = m1Flags,
            m2Flags = m2Flags,
            m3Flags = m3Flags,
            difficulty = difficulty,
            color = color,
            maleName = maleName,
            femaleName = femaleName
        )
    }

    fun createItem(
        name: String,
        symbol: String = "WEAPON_CLASS",
        color: String = "HI_METAL",
        description: String? = "A test item description.",
        material: String = "iron",
        subCategory: String = "P_SWORD",
        weight: Int = 40,
        value: Int = 15,
        smallDamage: Int = 8,
        largeDamage: Int = 12,
        ac: Int = 0,
        magicCancellation: Int = 0,
        magicItem: Boolean = false,
        charge: Boolean = false,
        merge: Boolean = false,
        unique: Boolean = false,
        tough: Boolean = false,
        notWish: Boolean = false,
        damageType: String = "AD_PHYS",
        zapDirection: String = "NODIR",
        probability: Int = 100,
        delay: Int = 0,
        hitBonus: Int = 0,
        spellLevel: Int = 0,
        nutrition: Int = 0,
        property: String = "0"
    ) = ItemEntity(
        name = name,
        description = description,
        merge = merge,
        magicItem = magicItem,
        charge = charge,
        unique = unique,
        notWish = notWish,
        tough = tough,
        damageType = damageType,
        zapDirection = zapDirection,
        subCategory = subCategory,
        material = material,
        property = property,
        symbol = symbol,
        probability = probability,
        delay = delay,
        weight = weight,
        value = value,
        smallDamage = smallDamage,
        largeDamage = largeDamage,
        ac = ac,
        hitBonus = hitBonus,
        magicCancellation = magicCancellation,
        spellLevel = spellLevel,
        nutrition = nutrition,
        color = color
    )

    fun createArtifact(
        name: String,
        baseItem: String = "long sword",
        alignment: String = "Lawful",
        role: String? = "Knight",
        wieldedProperties: String = "MR2_SEARCHING|MR_DRAIN",
        carriedProperties: String = "",
        attackToHitBonus: String? = "1d5",
        attackModifier: String? = "1d10",
        attackTypeModifier: String? = "AD_PHYS",
        damageTarget: String? = null,
        invokedPower: String? = null,
        specialEffects: String = "Manual search bonus|Demon hostility"
    ) = ArtifactEntity(
        name = name,
        baseItem = baseItem,
        alignment = alignment,
        role = role,
        wieldedProperties = wieldedProperties,
        carriedProperties = carriedProperties,
        attackToHitBonus = attackToHitBonus,
        attackModifier = attackModifier,
        attackTypeModifier = attackTypeModifier,
        damageTarget = damageTarget,
        invokedPower = invokedPower,
        specialEffects = specialEffects
    )

    fun createProperty(
        id: String,
        name: String,
        description: String = "Test property description.",
        summary: String = "Test summary.",
        type: String = "intrinsic"
    ) = PropertyEntity(
        id = id,
        name = name,
        description = description,
        summary = summary,
        type = type
    )
}
