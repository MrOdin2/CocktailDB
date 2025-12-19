import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { CocktailsComponent } from './cocktails.component';
import { ApiService } from '../../services/api.service';
import { ExportService } from '../../services/export.service';
import { MeasureService } from '../../services/measure.service';
import { TranslateService } from '../../services/translate.service';
import { FuzzySearchService } from '../../services/fuzzy-search.service';
import { of } from 'rxjs';
import { Cocktail, CocktailIngredient, Ingredient, IngredientType } from '../../models/models';

describe('CocktailsComponent - Drag and Drop', () => {
  let component: CocktailsComponent;
  let fixture: ComponentFixture<CocktailsComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let exportService: jasmine.SpyObj<ExportService>;
  let measureService: jasmine.SpyObj<MeasureService>;
  let translateService: jasmine.SpyObj<TranslateService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', [
      'getAllCocktails',
      'getAllIngredients',
      'getAvailableCocktails',
      'createCocktail',
      'updateCocktail',
      'deleteCocktail',
      'createIngredient'
    ]);
    const exportServiceSpy = jasmine.createSpyObj('ExportService', ['exportCocktails']);
    const measureServiceSpy = jasmine.createSpyObj('MeasureService', [
      'getUnit',
      'setUnit',
      'getAvailableUnits',
      'formatMeasure',
      'convertToMl'
    ]);
    const translateServiceSpy = jasmine.createSpyObj('TranslateService', ['translate', 'getLanguage', 'getCurrentLanguage'], {
      currentLanguage$: of('en')
    });

    // Set up default return values
    apiServiceSpy.getAllCocktails.and.returnValue(of([]));
    apiServiceSpy.getAllIngredients.and.returnValue(of([]));
    apiServiceSpy.getAvailableCocktails.and.returnValue(of([]));
    measureServiceSpy.getUnit.and.returnValue(of('ml'));
    measureServiceSpy.getAvailableUnits.and.returnValue(['ml', 'oz', 'cl']);
    measureServiceSpy.formatMeasure.and.returnValue('30 ml');
    measureServiceSpy.convertToMl.and.returnValue(30);
    translateServiceSpy.getLanguage.and.returnValue(of('en'));
    translateServiceSpy.getCurrentLanguage.and.returnValue('en');
    translateServiceSpy.translate.and.returnValue('translated');

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CocktailsComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        { provide: ExportService, useValue: exportServiceSpy },
        { provide: MeasureService, useValue: measureServiceSpy },
        { provide: TranslateService, useValue: translateServiceSpy },
        FuzzySearchService
      ]
    }).compileComponents();

    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    exportService = TestBed.inject(ExportService) as jasmine.SpyObj<ExportService>;
    measureService = TestBed.inject(MeasureService) as jasmine.SpyObj<MeasureService>;
    translateService = TestBed.inject(TranslateService) as jasmine.SpyObj<TranslateService>;

    fixture = TestBed.createComponent(CocktailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('dropStep', () => {
    it('should reorder steps when dropped', () => {
      // Arrange
      component.newCocktail.steps = ['Step 1', 'Step 2', 'Step 3'];
      const event = {
        previousIndex: 0,
        currentIndex: 2,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<string[]>;

      // Act
      component.dropStep(event);

      // Assert
      expect(component.newCocktail.steps).toEqual(['Step 2', 'Step 3', 'Step 1']);
    });

    it('should handle reordering from end to beginning', () => {
      // Arrange
      component.newCocktail.steps = ['Step 1', 'Step 2', 'Step 3'];
      const event = {
        previousIndex: 2,
        currentIndex: 0,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<string[]>;

      // Act
      component.dropStep(event);

      // Assert
      expect(component.newCocktail.steps).toEqual(['Step 3', 'Step 1', 'Step 2']);
    });

    it('should handle dropping at same position', () => {
      // Arrange
      const originalSteps = ['Step 1', 'Step 2', 'Step 3'];
      component.newCocktail.steps = [...originalSteps];
      const event = {
        previousIndex: 1,
        currentIndex: 1,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<string[]>;

      // Act
      component.dropStep(event);

      // Assert
      expect(component.newCocktail.steps).toEqual(originalSteps);
    });
  });

  describe('dropIngredient', () => {
    it('should reorder ingredients when dropped', () => {
      // Arrange
      const ingredients: CocktailIngredient[] = [
        { ingredientId: 1, measureMl: 30 },
        { ingredientId: 2, measureMl: 60 },
        { ingredientId: 3, measureMl: 90 }
      ];
      component.newCocktail.ingredients = [...ingredients];
      const event = {
        previousIndex: 0,
        currentIndex: 2,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<CocktailIngredient[]>;

      // Act
      component.dropIngredient(event);

      // Assert
      expect(component.newCocktail.ingredients).toEqual([
        { ingredientId: 2, measureMl: 60 },
        { ingredientId: 3, measureMl: 90 },
        { ingredientId: 1, measureMl: 30 }
      ]);
    });

    it('should handle reordering from end to beginning', () => {
      // Arrange
      const ingredients: CocktailIngredient[] = [
        { ingredientId: 1, measureMl: 30 },
        { ingredientId: 2, measureMl: 60 },
        { ingredientId: 3, measureMl: 90 }
      ];
      component.newCocktail.ingredients = [...ingredients];
      const event = {
        previousIndex: 2,
        currentIndex: 0,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<CocktailIngredient[]>;

      // Act
      component.dropIngredient(event);

      // Assert
      expect(component.newCocktail.ingredients).toEqual([
        { ingredientId: 3, measureMl: 90 },
        { ingredientId: 1, measureMl: 30 },
        { ingredientId: 2, measureMl: 60 }
      ]);
    });

    it('should handle dropping at same position', () => {
      // Arrange
      const ingredients: CocktailIngredient[] = [
        { ingredientId: 1, measureMl: 30 },
        { ingredientId: 2, measureMl: 60 },
        { ingredientId: 3, measureMl: 90 }
      ];
      component.newCocktail.ingredients = [...ingredients];
      const event = {
        previousIndex: 1,
        currentIndex: 1,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<CocktailIngredient[]>;

      // Act
      component.dropIngredient(event);

      // Assert
      expect(component.newCocktail.ingredients).toEqual(ingredients);
    });

    it('should preserve ingredient data after reordering', () => {
      // Arrange
      const ingredient1 = { ingredientId: 10, measureMl: 45 };
      const ingredient2 = { ingredientId: 20, measureMl: 30 };
      component.newCocktail.ingredients = [ingredient1, ingredient2];
      const event = {
        previousIndex: 0,
        currentIndex: 1,
        item: {} as any,
        container: {} as any,
        previousContainer: {} as any,
        isPointerOverContainer: true,
        distance: { x: 0, y: 0 },
        dropPoint: { x: 0, y: 0 },
        event: {} as any
      } as CdkDragDrop<CocktailIngredient[]>;

      // Act
      component.dropIngredient(event);

      // Assert
      expect(component.newCocktail.ingredients[0]).toEqual(ingredient2);
      expect(component.newCocktail.ingredients[1]).toEqual(ingredient1);
      expect(component.newCocktail.ingredients[0].ingredientId).toBe(20);
      expect(component.newCocktail.ingredients[0].measureMl).toBe(30);
    });
  });

  describe('Fuzzy Search', () => {
    beforeEach(() => {
      const testCocktails: Cocktail[] = [
        { id: 1, name: 'Mojito', ingredients: [], steps: [], tags: ['rum', 'mint'], abv: 10, baseSpirit: 'Rum' },
        { id: 2, name: 'Martini', ingredients: [], steps: [], tags: ['gin', 'classic'], abv: 25, baseSpirit: 'Gin' },
        { id: 3, name: 'Manhattan', ingredients: [], steps: [], tags: ['whiskey', 'classic'], abv: 30, baseSpirit: 'Whiskey' }
      ];
      apiService.getAllCocktails.and.returnValue(of(testCocktails));
      component.ngOnInit();
    });

    it('should find cocktails with exact name match', () => {
      component.nameFilter = 'Mojito';
      const displayed = component.displayedCocktails;
      expect(displayed.length).toBe(1);
      expect(displayed[0].name).toBe('Mojito');
    });

    it('should find cocktails with typo in name (fuzzy match)', () => {
      component.nameFilter = 'Mojto'; // Missing 'i'
      const displayed = component.displayedCocktails;
      expect(displayed.length).toBeGreaterThan(0);
      expect(displayed.some(c => c.name === 'Mojito')).toBe(true);
    });

    it('should find cocktails with partial name match', () => {
      component.nameFilter = 'Man';
      const displayed = component.displayedCocktails;
      expect(displayed.some(c => c.name === 'Manhattan')).toBe(true);
    });

    it('should find cocktails by tag with typo (fuzzy match)', () => {
      component.tagFilter = 'clasic'; // Missing 's'
      const displayed = component.displayedCocktails;
      expect(displayed.length).toBeGreaterThan(0);
      expect(displayed.some(c => c.tags.includes('classic'))).toBe(true);
    });

    it('should filter ingredients with fuzzy search', () => {
      const testIngredients: Ingredient[] = [
        { id: 1, name: 'Vodka', type: IngredientType.SPIRIT, abv: 40, inStock: true },
        { id: 2, name: 'Gin', type: IngredientType.SPIRIT, abv: 40, inStock: true },
        { id: 3, name: 'Rum', type: IngredientType.SPIRIT, abv: 40, inStock: true }
      ];
      apiService.getAllIngredients.and.returnValue(of(testIngredients));
      component.loadIngredients();
      
      component.ingredientSearchFilter = 'Vokda'; // Typo
      const filtered = component.filteredIngredients;
      expect(filtered.length).toBeGreaterThan(0);
      expect(filtered.some(i => i.name === 'Vodka')).toBe(true);
    });

    it('should sort results by fuzzy search score (best match first)', () => {
      const testCocktails: Cocktail[] = [
        { id: 1, name: 'Gin and Tonic', ingredients: [], steps: [], tags: [], abv: 10, baseSpirit: 'Gin' },
        { id: 2, name: 'Vodka Tonic', ingredients: [], steps: [], tags: [], abv: 10, baseSpirit: 'Vodka' },
        { id: 3, name: 'Vodka Gimlet', ingredients: [], steps: [], tags: [], abv: 15, baseSpirit: 'Vodka' }
      ];
      apiService.getAllCocktails.and.returnValue(of(testCocktails));
      component.ngOnInit();
      
      component.nameFilter = 'Vodka Tonic';
      const displayed = component.displayedCocktails;
      
      // Vodka Tonic should be first (exact match)
      expect(displayed.length).toBeGreaterThan(0);
      expect(displayed[0].name).toBe('Vodka Tonic');
    });
  });

  describe('Reordered data persistence', () => {
    it('should persist reordered steps when creating cocktail', (done) => {
      // Arrange
      component.newCocktail = {
        name: 'Test Cocktail',
        ingredients: [{ ingredientId: 1, measureMl: 30 }],
        steps: ['Step 2', 'Step 1', 'Step 3'],
        notes: '',
        tags: [],
        abv: 0,
        baseSpirit: 'none'
      };
      apiService.createCocktail.and.returnValue(of({} as Cocktail));

      // Act
      component.createCocktail();

      // Assert
      setTimeout(() => {
        expect(apiService.createCocktail).toHaveBeenCalledWith(
          jasmine.objectContaining({
            steps: ['Step 2', 'Step 1', 'Step 3']
          })
        );
        done();
      }, 100);
    });

    it('should persist reordered ingredients when creating cocktail', (done) => {
      // Arrange
      const reorderedIngredients = [
        { ingredientId: 3, measureMl: 90 },
        { ingredientId: 1, measureMl: 30 },
        { ingredientId: 2, measureMl: 60 }
      ];
      component.newCocktail = {
        name: 'Test Cocktail',
        ingredients: reorderedIngredients,
        steps: ['Mix'],
        notes: '',
        tags: [],
        abv: 0,
        baseSpirit: 'none'
      };
      apiService.createCocktail.and.returnValue(of({} as Cocktail));

      // Act
      component.createCocktail();

      // Assert
      setTimeout(() => {
        expect(apiService.createCocktail).toHaveBeenCalledWith(
          jasmine.objectContaining({
            ingredients: reorderedIngredients
          })
        );
        done();
      }, 100);
    });

    it('should persist reordered data when updating cocktail', (done) => {
      // Arrange
      component.isEditMode = true;
      component.editingCocktailId = 5;
      component.newCocktail = {
        name: 'Updated Cocktail',
        ingredients: [
          { ingredientId: 2, measureMl: 60 },
          { ingredientId: 1, measureMl: 30 }
        ],
        steps: ['Step 3', 'Step 1', 'Step 2'],
        notes: 'Reordered',
        tags: ['test'],
        abv: 15,
        baseSpirit: 'vodka'
      };
      apiService.updateCocktail.and.returnValue(of({} as Cocktail));

      // Act
      component.createCocktail();

      // Assert
      setTimeout(() => {
        expect(apiService.updateCocktail).toHaveBeenCalledWith(
          5,
          jasmine.objectContaining({
            ingredients: [
              { ingredientId: 2, measureMl: 60 },
              { ingredientId: 1, measureMl: 30 }
            ],
            steps: ['Step 3', 'Step 1', 'Step 2']
          })
        );
        done();
      }, 100);
    });
  });
});
