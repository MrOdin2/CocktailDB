export const de = {
  common: {
    back: 'Zurück',
    backTo: 'Zurück zu {{ destination }}',
    cancel: 'Abbrechen',
    save: 'Sichern des momentanen Zustands',
    edit: 'Bearbeiten',
    delete: 'Löschen',
    add: 'Hinzufügen',
    remove: 'Entfernen',
    search: 'Suchen',
    filter: 'Filtern',
    clear: 'Löschen',
    clearFilters: 'Filter zurücksetzen',
    loading: 'Laden...',
    retry: 'Erneut versuchen',
    yes: 'Ja',
    no: 'Nein',
    active: 'AKTIV',
    all: 'Alle',
    none: 'Keine',
    name: 'Name',
    type: 'Typ',
    actions: 'Aktionen',
    logout: 'Abmelden',
    login: 'Anmelden',
    cocktails: 'Cocktails',
    ingredients: 'Zutaten',
    settings: 'Einstellungen',
    visualizations: 'Visualisierungen',
    notes: 'Notizen',
    tags: 'Tags',
    instructions: 'Anleitung',
    next: 'Weiter',
    export: 'Exportieren'
  },
  app: {
    title: 'CocktailDB',
    adminMode: 'Admin-Modus'
  },
  login: {
    title: 'Anmelden',
    selectRole: 'Rolle auswählen:',
    admin: 'Möglichecocktailsauszutatenautomatikanwendungsadministratorzugang',
    barkeeper: 'Möglichecocktailsauszutatenautomatikanwendungsbarmannzugang',
    enterPassword: '{{ role }}-Schlüsselwort eingeben',
    loggingIn: 'Anmeldung ist in bearbeutung...',
    loginAs: 'Als {{ role }} anmelden'
  },
  nav: {
    ingredients: 'Zutaten',
    cocktails: 'Cocktails',
    visualizations: 'Visualisierungen',
    settings: 'Einstellungen'
  },
  settings: {
    title: 'Einstellungen',
    language: {
      title: 'Sprache',
      description: 'Wähle deine bevorzugte Sprache:',
      note: 'Diese Einstellung wird lokal in deinem Browser gespeichert.'
    },
    measureUnit: {
      title: 'Maßeinheit',
      description: 'Wähle deine bevorzugte Einheit für die Anzeige von Zutatenmengen:',
      note: 'Diese Einstellung wird lokal in deinem Browser gespeichert.',
      ml: {
        name: 'Milliliter (ml)',
        description: 'Metrisches System - international gebräuchlich',
        example: '30 ml, 60 ml'
      },
      oz: {
        name: 'Unzen (oz)',
        description: 'Imperiales System - in den USA gebräuchlich',
        example: '1 oz, 2 oz'
      },
      cl: {
        name: 'Zentiliter (cl)',
        description: 'Metrisches System - in Europa gebräuchlich',
        example: '3 cl, 6 cl'
      }
    },
    theme: {
      title: 'Globale Themenauswahl',
      description: 'Wähle das Thema für alle Benutzer (Administratoren, Barkeeper und Besucher):',
      currentTheme: 'Aktuelles Thema',
      applyTheme: 'Thema anwenden',
      note: 'Das ausgewählte Thema wird global für alle Benutzer und Besucher deiner Bar angewendet.',
      basic: {
        name: 'Standard',
        description: 'Professionelles und unauffälliges Erscheinungsbild',
        preview: 'Weißer Hintergrund mit standard dunkelgrauem Text und minimalem Styling'
      },
      terminalGreen: {
        name: 'Terminal Grün',
        description: 'Klassisches 80er Jahre monochromes grünes Terminal',
        preview: 'Schwarzer Hintergrund mit hellgrünem (#00ff00) Text'
      },
      cyberpunk: {
        name: 'Cyberpunk Magenta/Cyan',
        description: 'Neon-Cyberpunk-Ästhetik (RoboCop, Blade Runner, Matrix)',
        preview: 'Dunkelblauer (#0a0e27) Hintergrund mit Cyan und Magenta Neon-Akzenten'
      },
      amber: {
        name: 'Bernstein-Monitor',
        description: 'Vintage Bernstein-CRT-Monitor',
        preview: 'Dunkelbrauner (#1a0f00) Hintergrund mit warmem Bernstein/Orange (#ffb000) Text'
      }
    },
    about: {
      title: 'Über Themen',
      basicDescription: 'Das Standard-Thema ist das voreingestellte professionelle Erscheinungsbild.',
      retroFeatures: 'Optionale Retro-Themen bieten:',
      feature1: 'Authentische 80er Jahre Farbpaletten mit atmosphärischen Hintergründen',
      feature2: 'Courier New Monospace-Schrift für Terminal-Authentizität',
      feature3: 'CRT-Scanline-Overlay-Effekte',
      feature4: 'Textglüh-Effekte (Phosphorbildschirm-Simulation)',
      feature5: '80er Jahre blockige Rahmen (keine abgerundeten Ecken)',
      feature6: 'Hoher Kontrast für maximale Lesbarkeit'
    }
  },
  ingredients: {
    title: 'Zutaten',
    addNew: 'Neue Zutat hinzufügen',
    allIngredients: 'Alle Zutaten',
    searchByName: 'Nach Name suchen:',
    filterByName: 'Nach Name filtern...',
    filterByType: 'Nach Typ filtern:',
    allTypes: 'Alle Typen',
    abv: 'Alk.',
    abvPercent: 'Alkoholgehalt (%):',
    inStock: 'Auf Lager',
    nameRequired: 'Name ist erforderlich',
    addIngredient: 'Zutat hinzufügen',
    substitutes: 'Ersatzstoffe',
    alternatives: 'Alternativen',
    substitutesHelp: 'Zutaten, die als direkte Ersatzstoffe verwendet werden können (z.B. generisch vs. Marke)',
    alternativesHelp: 'Merklich unterschiedliche Zutaten, die trotzdem verwendet werden können (z.B. Champagner vs. Prosecco)',
    noIngredientsAvailable: 'Noch keine anderen Zutaten verfügbar. Fügen Sie zuerst weitere Zutaten hinzu.',
    editRelationshipsInfo: 'Ersatzstoffe/Alternativen unten bearbeiten',
    searchIngredients: 'Zutaten suchen...',
    noMatch: 'Keine passenden Zutaten',
    modal: {
      title: 'Neue Zutat hinzufügen'
    }
  },
  cocktails: {
    title: 'Cocktails',
    addNew: 'Neuen Cocktail hinzufügen',
    exportCocktails: 'Cocktails exportieren',
    allCocktails: 'Alle Cocktails',
    availableCocktails: 'Verfügbare Cocktails',
    searchByName: 'Nach Name suchen:',
    filterByName: 'Nach Name filtern...',
    filterBySpirit: 'Nach Spirituose filtern:',
    filterByTag: 'Nach Tag filtern:',
    allSpirits: 'Alle Spirituosen',
    allTags: 'Alle Tags',
    availableOnly: 'Nur Verfügbare',
    cocktailName: 'Cocktailname:',
    cocktailNameRequired: 'Cocktailname ist erforderlich',
    ingredientsLabel: 'Zutaten:',
    searchIngredients: 'Zutaten suchen...',
    selectIngredient: 'Zutat auswählen',
    amount: 'Menge',
    count: 'Anzahl',
    items: 'Stück',
    newIngredient: '+ Neue Zutat',
    ingredientRequired: 'Mindestens eine Zutat ist erforderlich',
    steps: 'Schritte:',
    addStep: 'Schritt hinzufügen',
    addStepButton: 'Schritt hinzufügen',
    notesLabel: 'Notizen:',
    tagsLabel: 'Tags:',
    selectTag: 'Tag auswählen oder neu eingeben',
    typeNewTag: 'Oder neuen Tag eingeben',
    addTag: 'Tag hinzufügen',
    updateCocktail: 'Cocktail aktualisieren',
    addCocktail: 'Cocktail hinzufügen',
    editCocktail: 'Cocktail bearbeiten',
    substitute: 'Ersatz',
    alternative: 'Alternative',
    usesSubstitutes: 'Dieser Cocktail kann mit Ersatzzutaten zubereitet werden',
    usesAlternatives: 'Dieser Cocktail kann mit alternativen Zutaten zubereitet werden',
    export: {
      title: 'Cocktails exportieren',
      exportType: 'Exporttyp:',
      menuType: 'Menü (nur Name + Zutaten)',
      cheatsheetType: 'Spickzettel (Vollständiges Rezept mit Schritten)',
      groupBy: 'Gruppieren nach:',
      groupBySpirit: 'Spirituose',
      groupByTags: 'Tags',
      format: 'Format:',
      html: 'HTML',
      markdown: 'Markdown (.md)',
      pdf: 'PDF (Als PDF drucken)',
      exportingCount: '{{ count }} Cocktail(s) werden basierend auf aktuellen Filtern exportiert.',
      tagSelection: {
        title: 'Tags für Gruppierung auswählen',
        info: 'Wähle aus, welche Tags für die Gruppierung verwendet werden sollen und ordne sie:',
        availableTags: 'Verfügbare Tags:',
        selectedTags: 'Ausgewählte Tags (in Reihenfolge):'
      }
    }
  },
  visualizations: {
    title: 'Datenvisualisierungen',
    ingredientsTab: 'Zutaten',
    cocktailsTab: 'Cocktails',
    comingSoon: 'Cocktail-Visualisierungen demnächst verfügbar!'
  },
  barkeeper: {
    title: 'CocktailDB',
    mode: 'Barkeeper-Modus',
    menu: {
      title: 'Barkeeper-Menü',
      showOnlyAvailable: 'Nur verfügbare Cocktails anzeigen',
      findByLetter: 'Nach Buchstabe suchen',
      availableCocktails: 'Verfügbare Cocktails',
      randomCocktail: 'Zufälliger Cocktail',
      manageStock: 'Lagerbestand verwalten'
    },
    alphabet: {
      title: 'Ersten Buchstaben wählen',
      loading: 'Cocktails werden geladen...'
    },
    cocktailList: {
      loading: 'Cocktails werden geladen...',
      noCocktails: 'Keine Cocktails gefunden',
      filter: 'Filtern',
      baseSpirit: 'Basisspirituose:',
      tag: 'Tag:',
      abv: 'Alk.:',
      allSpirits: 'Alle Spirituosen',
      allTags: 'Alle Tags',
      allAbv: 'Alle Alkoholgehalte',
      nonAlcoholic: 'Alkoholfrei (0%)',
      lowAbv: 'Niedrig (1-10%)',
      mediumAbv: 'Mittel (11-25%)',
      highAbv: 'Hoch (25%+)'
    },
    recipe: {
      title: 'Rezept',
      base: 'Basis:',
      abv: 'Alk.:',
      ingredients: 'Zutaten:',
      instructions: 'Anleitung:',
      notes: 'Notizen:',
      tags: 'Tags:',
      usesSubstitutes: 'Verwendet Ersatzstoffe',
      usesAlternatives: 'Verwendet Alternativen',
      outOfStock: 'Nicht vorrätig',
      substituteWith: 'Ersetzen mit',
      alternativeWith: 'Alternative'
    },
    random: {
      title: 'Zufälliger Cocktail',
      typeLabel: 'Typ:',
      all: 'Alle',
      alcoholic: 'Alkoholisch',
      nonAlcoholic: 'Alkoholfrei',
      baseSpiritLabel: 'Basisspirituose:',
      allSpirits: 'Alle Spirituosen',
      pickRandom: '🎲 Zufälligen Cocktail wählen'
    },
    stock: {
      title: 'Lagerbestand verwalten',
      loading: 'Zutaten werden geladen...',
      inStock: 'Auf Lager',
      outOfStock: 'Nicht auf Lager'
    }
  },
  visitor: {
    title: 'CocktailDB',
    tagline: 'Möglichecocktailsauszutatenautomatikanwendung',
    menu: {
      whatCanWeMake: 'Welche Cocktails können mit dem momentan laut System verfügbaren Zutaten hergestellt werden?',
      browseCocktails: 'Liste der verfügbaren Cocktails durchsuchen',
      browseDescription: 'Cocktails durch Namen oder Zutaten bestimmen',
      categories: 'Kategorien',
      categoriesDescription: 'Suchen durch nicht-objektive Kategorisierungen',
      surpriseMe: 'Überrasche mich!',
      surpriseDescription: 'Automatische auswahl eines zufälligen, aus den momentan laut System verfügbaren Zutaten herstellbaren, Cocktails',
      welcome: 'Willkommen!',
      welcomeText: 'Dies ist eine öffentliche Ansicht, die Cocktails zeigt, die mit den aktuell verfügbaren Zutaten gemacht werden können. Offnungszeiten Werktags 19:07 - 19:13 Uhr.',
      loginPrompt: 'Zugang nur für Befugte. Eltern haften für ihre Kinder.',
      loginHere: 'Anmeldungsvorgang starten'
    },
    cocktailList: {
      title: 'Verfügbare Cocktails',
      searchPlaceholder: 'Nach Name, Spirituose oder Tag suchen...',
      loading: 'Cocktails werden geladen...',
      showing: 'Zeige',
      of: 'von',
      availableCocktails: 'verfügbaren Cocktails',
      noMatch: 'Keine Cocktails entsprechen deiner Suche.',
      clearSearch: 'Suche löschen',
      noCocktails: 'Derzeit sind keine Cocktails verfügbar.',
      comeBackLater: 'Die Lager sind leer.',
      viewRecipe: 'Rezept ansehen →',
      baseSpirit: 'Basisspirituose:',
      abv: 'Alkoholgehalt:',
      alcoholic: 'Alkoholisch',
      nonAlcoholic: 'Alkoholfrei (beispielsweise Radler)',
      lowAlcohol: 'Wenig Alkohol (beispielsweise Kölsch)'
    },
    recipe: {
      backToCocktails: 'Zurück zu Cocktails',
      loading: 'Rezept wird geladen...',
      goBack: 'Zurück',
      tags: '🏷️ Tags',
      ingredients: '📝 Zutaten',
      instructions: '🍸 Anleitung',
      notes: '💡 Notizen',
      usesSubstitutes: 'Verwendet Ersatzstoffe',
      usesAlternatives: 'Verwendet Alternativen',
      outOfStock: 'Nicht vorrätig',
      substituteWith: 'Ersetzen mit',
      alternativeWith: 'Alternative'
    },
    random: {
      title: '🎲 Überrasch mich!',
      loading: 'Cocktails werden geladen...',
      filterOptions: 'Filteroptionen',
      type: 'Typ:',
      allCocktails: 'Alle Cocktails',
      alcoholicOnly: 'Nur Alkoholische',
      nonAlcoholicOnly: 'Nur Alkoholfreie',
      baseSpirit: 'Basisspirituose:',
      anySpirit: 'Beliebige Spirituose',
      resetFilters: 'Filter zurücksetzen',
      pickRandom: '🎲 Zufälligen Cocktail wählen',
      available: 'Cocktails mit aktuellen Filtern verfügbar',
      viewFullRecipe: 'Vollständiges Rezept ansehen →',
      noCocktails: 'Derzeit sind keine Cocktails verfügbar.',
      comeBackLater: 'Komm später wieder, wenn wir Zutaten auf Lager haben!'
    },
    categories: {
      title: 'Kategorien durchsuchen',
      loading: 'Kategorien werden geladen...',
      cocktails: 'Cocktails',
      found: 'Gefunden',
      noCocktails: 'Keine Cocktails in dieser Kategorie gefunden.',
      noCocktailsAvailable: 'Derzeit sind keine Cocktails verfügbar.',
      comeBackLater: 'Komm später wieder, wenn wir Zutaten auf Lager haben!'
    }
  }
};
