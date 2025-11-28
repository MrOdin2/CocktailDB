import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MeasureUnit } from '../models/models';

const STORAGE_KEY = 'cocktaildb-measure-unit';

@Injectable({
  providedIn: 'root'
})
export class MeasureService {
  private currentUnit = new BehaviorSubject<MeasureUnit>(MeasureUnit.ML);

  // Conversion constants
  private readonly ML_PER_OZ = 29.5735;
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
   * Format a measurement in ml to a display string in the current unit
   */
  formatMeasure(ml: number, unit: MeasureUnit = this.currentUnit.getValue()): string {
    const converted = this.convertFromMl(ml, unit);
    const decimals = unit === MeasureUnit.OZ ? 2 : 1;
    const rounded = Math.round(converted * Math.pow(10, decimals)) / Math.pow(10, decimals);
    
    // Remove trailing zeros for cleaner display
    const formatted = rounded.toString().replace(/\.?0+$/, '');
    return `${formatted} ${unit}`;
  }

  /**
   * Get all available units
   */
  getAvailableUnits(): MeasureUnit[] {
    return Object.values(MeasureUnit);
  }
}
