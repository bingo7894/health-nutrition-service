CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Table for master food database & barcode lookups
CREATE TABLE IF NOT EXISTS food_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    barcode VARCHAR(64) UNIQUE,
    name_th VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    brand VARCHAR(128),
    serving_size_grams DOUBLE PRECISION DEFAULT 100.0,
    calories INTEGER NOT NULL,
    protein_g DOUBLE PRECISION DEFAULT 0.0,
    carbs_g DOUBLE PRECISION DEFAULT 0.0,
    fat_g DOUBLE PRECISION DEFAULT 0.0,
    sodium_mg DOUBLE PRECISION DEFAULT 0.0,
    sugar_g DOUBLE PRECISION DEFAULT 0.0,
    fiber_g DOUBLE PRECISION DEFAULT 0.0,
    image_url TEXT,
    source VARCHAR(32) DEFAULT 'SYSTEM', -- 'OPEN_FOOD_FACTS', 'AI_SCAN', 'USDA', 'SYSTEM'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table for daily user summary logs
CREATE TABLE IF NOT EXISTS daily_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    log_date DATE NOT NULL,
    total_calories_in INTEGER DEFAULT 0,
    total_protein_g DOUBLE PRECISION DEFAULT 0.0,
    total_carbs_g DOUBLE PRECISION DEFAULT 0.0,
    total_fat_g DOUBLE PRECISION DEFAULT 0.0,
    total_sodium_mg DOUBLE PRECISION DEFAULT 0.0,
    total_sugar_g DOUBLE PRECISION DEFAULT 0.0,
    total_fiber_g DOUBLE PRECISION DEFAULT 0.0,
    total_calories_burned INTEGER DEFAULT 0,
    net_calories INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_log_date UNIQUE (user_id, log_date)
);

-- 3. Table for individual meal items logged by users
CREATE TABLE IF NOT EXISTS meal_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id UUID REFERENCES daily_logs(id) ON DELETE CASCADE,
    meal_type VARCHAR(32) NOT NULL, -- 'BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'
    food_name VARCHAR(255) NOT NULL,
    portion_weight_grams DOUBLE PRECISION NOT NULL,
    cooking_method VARCHAR(32) DEFAULT 'BOILED', -- 'BOILED', 'GRILLED', 'STIR_FRIED', 'DEEP_FRIED'
    calories INTEGER NOT NULL,
    protein_g DOUBLE PRECISION DEFAULT 0.0,
    carbs_g DOUBLE PRECISION DEFAULT 0.0,
    fat_g DOUBLE PRECISION DEFAULT 0.0,
    sodium_mg DOUBLE PRECISION DEFAULT 0.0,
    image_url TEXT,
    logged_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Table for 1-click favorite combos (quick logging without AI)
CREATE TABLE IF NOT EXISTS favorite_combos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    combo_name VARCHAR(255) NOT NULL,
    description TEXT,
    total_calories INTEGER NOT NULL,
    total_protein_g DOUBLE PRECISION DEFAULT 0.0,
    total_carbs_g DOUBLE PRECISION DEFAULT 0.0,
    total_fat_g DOUBLE PRECISION DEFAULT 0.0,
    items_json TEXT, -- JSON array of ingredient details
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_food_barcode ON food_items(barcode);
CREATE INDEX IF NOT EXISTS idx_daily_logs_user_date ON daily_logs(user_id, log_date);

