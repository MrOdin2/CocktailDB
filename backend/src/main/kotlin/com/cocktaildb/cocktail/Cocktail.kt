package com.cocktaildb.cocktail

import com.cocktaildb.cocktail.CocktailIngredient
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "cocktails")
@Schema(description = "Cocktail recipe with ingredients and preparation steps")
data class Cocktail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    val id: Long? = null,

    @Column(nullable = false)
    @Schema(description = "Cocktail name", required = true, example = "Mojito")
    var name: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_ingredients", joinColumns = [JoinColumn(name = "cocktail_id")])
    @Schema(description = "List of ingredients with measurements", required = true)
    var ingredients: MutableList<CocktailIngredient> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_steps", joinColumns = [JoinColumn(name = "cocktail_id")])
    @Column(name = "step")
    @Schema(description = "Preparation steps in order", required = true, example = "[\"Muddle mint and lime\", \"Add rum and ice\", \"Top with soda water\"]")
    var steps: MutableList<String> = mutableListOf(),

    @Column(length = 1000)
    @Schema(description = "Additional notes or tips", example = "Best served in a highball glass")
    var notes: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_tags", joinColumns = [JoinColumn(name = "cocktail_id")])
    @Column(name = "tag")
    @Schema(description = "Tags for categorization", example = "[\"refreshing\", \"summer\", \"classic\"]")
    var tags: MutableList<String> = mutableListOf(),

    @Column(nullable = false)
    @Schema(description = "Alcohol by volume percentage (automatically calculated)", accessMode = Schema.AccessMode.READ_ONLY, example = "12")
    var abv: Int = 0,

    @Column(nullable = false, length = 50)
    @Schema(description = "Base spirit type (automatically calculated)", accessMode = Schema.AccessMode.READ_ONLY, example = "rum")
    var baseSpirit: String = "none",

    @Column(length = 50)
    @Schema(description = "Type of glassware required for serving", example = "highball")
    var glasswareType: String? = null,

    @Column(length = 50)
    @Schema(description = "Type of ice required", example = "cubed")
    var iceType: String? = null
)