-- V2__seed_thai_barcodes.sql
-- Seed popular Thai convenience store products for instant free barcode scanning

INSERT INTO food_items (barcode, name_th, name_en, brand, serving_size_grams, calories, protein_g, carbs_g, fat_g, sodium_mg, sugar_g, fiber_g, image_url, source)
VALUES
    ('8850188800012', 'มาม่า บะหมี่กึ่งสำเร็จรูป รสต้มยำกุ้ง', 'Mama Instant Noodles Tom Yum Kung Flavor', 'มาม่า (Mama)', 55.0, 260, 5.0, 36.0, 11.0, 1280.0, 3.0, 2.0, 'https://images.openfoodfacts.org/images/products/885/018/880/0012/front_th.jpg', 'SYSTEM'),
    ('8850267111077', 'นมถั่วเหลือง แลคตาซอย สูตรหวาน 250 มล.', 'Lactasoy Soy Milk Original Classic 250ml', 'แลคตาซอย (Lactasoy)', 250.0, 180, 7.0, 24.0, 6.0, 110.0, 18.0, 1.0, 'https://images.openfoodfacts.org/images/products/885/026/711/1077/front_th.jpg', 'SYSTEM'),
    ('8850329000012', 'นมพาสเจอร์ไรส์ เมจิ รสจืด 200 มล.', 'CP-Meiji Plain Pasteurized Milk 200ml', 'ซีพี-เมจิ (CP-Meiji)', 200.0, 130, 6.0, 9.0, 7.0, 90.0, 9.0, 0.0, 'https://images.openfoodfacts.org/images/products/885/032/900/0012/front_th.jpg', 'SYSTEM'),
    ('8851959132014', 'เครื่องดื่มวิตามินซี ซี-วิท รสเลมอน 140 มล.', 'C-Vitt Vitamin C Lemon 140ml', 'ซี-วิท (C-Vitt)', 140.0, 35, 0.0, 8.0, 0.0, 45.0, 7.0, 0.0, 'https://images.openfoodfacts.org/images/products/885/195/913/2014/front_th.jpg', 'SYSTEM'),
    ('8850999001004', 'สิงห์ น้ำดื่มโซดา 325 มล.', 'Singha Soda Water 325ml', 'สิงห์ (Singha)', 325.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 'https://images.openfoodfacts.org/images/products/885/099/900/1004/front_th.jpg', 'SYSTEM'),
    ('8850123456789', 'บีทาเก้น นมเปรี้ยวไขมันต่ำ รสส้ม 300 มล.', 'Betagen Low Fat Orange Fermented Milk 300ml', 'บีทาเก้น (Betagen)', 300.0, 160, 4.0, 34.0, 1.0, 75.0, 28.0, 0.0, 'https://images.openfoodfacts.org/images/products/885/012/345/6789/front_th.jpg', 'SYSTEM')
ON CONFLICT (barcode) DO NOTHING;

