import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MeasureUnit } from '../models/models';

const STORAGE_KEY = 'cocktaildb-measure-unit';

@Injectable({
  providedIn: 'root'
})
export class MeasureService {
  private currentUnit = new BehaviorSubject<MeasureUnit>(MeasureUnit.ML);

  // Conversion constants (exact values)
  private readonly ML_PER_OZ = 29.5735296875;
  private readonly ML_PER_CL = 10;

  constructor() {
    this.loadPreference();
  }

  private loadPreference(): void {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored && Object.values(MeasureUnit).includes(stored as MeasureUnit)) {
      this.currentUnit.next(stored as MeasureUnit);
    }
  }

  getUnit(): Observable<MeasureUnit> {
    return this.currentUnit.asObservable();
  }

  getCurrentUnit(): MeasureUnit {
    return this.currentUnit.getValue();
  }

  setUnit(unit: MeasureUnit): void {
    this.currentUnit.next(unit);
    localStorage.setItem(STORAGE_KEY, unit);
  }

  /**
   * Convert ml to the target unit
   */
  convertFromMl(ml: number, targetUnit: MeasureUnit = this.currentUnit.getValue()): number {
    switch (targetUnit) {
      case MeasureUnit.OZ:
        return ml / this.ML_PER_OZ;
      case MeasureUnit.CL:
        return ml / this.ML_PER_CL;
      case MeasureUnit.ML:
      default:
        return ml;
    }
  }

  /**
   * Convert from the source unit to ml
   */
  convertToMl(value: number, sourceUnit: MeasureUnit = this.currentUnit.getValue()): number {
    switch (sourceUnit) {
      case MeasureUnit.OZ:
        return value * this.ML_PER_OZ;
      case MeasureUnit.CL:
        return value * this.ML_PER_CL;
      case MeasureUnit.ML:
      default:
        return value;
    }
  }

  /**
   * Round to nearest 0.25 for oz, 0.5 for ml/cl
   */
  private roundToResolution(value: number, unit: MeasureUnit): number {
    const resolution = unit === MeasureUnit.OZ ? 0.25 : 0.5;
    return Math.round(value / resolution) * resolution;
  }

  /**
   * Format a measurement in ml to a display string in the current unit
   * Returns empty string for -1 (non-fluid ingredients like garnishes)
   */
  formatMeasure(ml: number, unit: MeasureUnit = this.currentUnit.getValue()): string {
    // Return empty string for -1 (non-fluid ingredients like garnishes, leaves, etc.)
    if (ml < 0) {
      return '';
    }
    
    const converted = this.convertFromMl(ml, unit);
    const rounded = this.roundToResolution(converted, unit);
    
    // Format with appropriate decimals based on resolution
    let formatted: string;
    if (unit === MeasureUnit.OZ) {
      // For oz, show up to 2 decimals (for 0.25 increments)
      formatted = rounded.toFixed(2).replace(/\.?0+$/, '');
    } else {
      // For ml/cl, show up to 1 decimal (for 0.5 increments)
      formatted = rounded.toFixed(1).replace(/\.?0+$/, '');
    }
    
    return `${formatted} ${unit}`;
  }

  /**
   * Get all available units
   */
  getAvailableUnits(): MeasureUnit[] {
    return Object.values(MeasureUnit);
  }
}
