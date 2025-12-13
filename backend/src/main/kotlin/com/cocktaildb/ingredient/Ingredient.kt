package com.cocktaildb.ingredient

import com.cocktaildb.ingredient.IngredientType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinTable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "ingredients")
class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: IngredientType,

    @Column(nullable = false)
    var abv: Int = 0,

    @Column(nullable = false)
    var inStock: Boolean = false,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ingredient_substitutes",
        joinColumns = [JoinColumn(name = "ingredient_id")],
        inverseJoinColumns = [JoinColumn(name = "substitute_id")]
    )
    var substitutes: MutableSet<Ingredient> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ingredient_alternatives",
        joinColumns = [JoinColumn(name = "ingredient_id")],
        inverseJoinColumns = [JoinColumn(name = "alternative_id")]
    )
    var alternatives: MutableSet<Ingredient> = mutableSetOf()
) {
    // Override equals and hashCode to only use id to avoid infinite loops
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ingredient) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Ingredient(id=$id, name='$name', type=$type, abv=$abv, inStock=$inStock)"
    }
}