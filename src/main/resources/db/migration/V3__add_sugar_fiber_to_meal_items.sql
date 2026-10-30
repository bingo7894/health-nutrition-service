-- V3__add_sugar_fiber_to_meal_items.sql
-- Add sugar_g and fiber_g to meal_items table for complete nutrient tracking

ALTER TABLE meal_items
ADD COLUMN IF NOT EXISTS sugar_g DOUBLE PRECISION DEFAULT 0.0,
ADD COLUMN IF NOT EXISTS fiber_g DOUBLE PRECISION DEFAULT 0.0;

