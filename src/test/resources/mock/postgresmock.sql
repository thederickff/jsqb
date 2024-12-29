CREATE TABLE users (
    id SERIAL PRIMARY KEY,               -- Unique identifier for each user (auto-increment)
    name VARCHAR(100) NOT NULL,           -- Name of the user
    email VARCHAR(150) NOT NULL UNIQUE,   -- Email address (must be unique)
    password VARCHAR(255) NOT NULL,       -- Password (hashed for security)
    role VARCHAR(255) DEFAULT 'user',     -- User role with predefined values
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp for record creation
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp for last update
);

CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,               -- Unique identifier for each address (auto-increment)
    street VARCHAR(255) NOT NULL,         -- Street address
    city VARCHAR(100) NOT NULL,           -- City
    state VARCHAR(100) NOT NULL,          -- State/Province/Region
    postalCode VARCHAR(20) NOT NULL,      -- Postal or ZIP code
    country VARCHAR(100) NOT NULL,        -- Country
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp for record creation
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp for last update
);

CREATE TABLE userAddress (
    userId INT REFERENCES users(id),     -- Foreign key to users table
    addressId INT REFERENCES addresses(id) -- Foreign key to addresses table
);

CREATE TABLE shops (
    id SERIAL PRIMARY KEY,                -- Unique identifier for each shop (auto-increment)
    name VARCHAR(150) NOT NULL,            -- Name of the shop
    ownerId INT NOT NULL REFERENCES users(id),  -- Foreign key to link to the owner (user)
    description TEXT,                     -- Optional description of the shop
    address VARCHAR(255),                  -- Shop's address
    phoneNumber VARCHAR(20),              -- Contact phone number
    email VARCHAR(150) UNIQUE,            -- Contact email (must be unique)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp for record creation
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp for last update
);

CREATE TABLE userShop (
    userId INT REFERENCES users(id),     -- Foreign key to users table
    shopId INT REFERENCES shops(id)      -- Foreign key to shops table
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,               -- Unique identifier for each product (auto-increment)
    shopId INT NOT NULL REFERENCES shops(id),  -- Foreign key to link the product to a shop
    name VARCHAR(150) NOT NULL,            -- Name of the product
    description TEXT,                     -- Optional description of the product
    price DECIMAL(10, 2) NOT NULL,         -- Price of the product (supports up to 99999999.99)
    stock INT DEFAULT 0,                  -- Number of items in stock
    category VARCHAR(100),                -- Category of the product
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp for record creation
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp for last update
);

CREATE TABLE sales (
    id SERIAL PRIMARY KEY,               -- Unique identifier for each sale (auto-increment)
    productId INT NOT NULL REFERENCES products(id), -- Foreign key linking to the Products table
    userId INT NOT NULL REFERENCES users(id),      -- Foreign key linking to the Users table (buyer)
    quantity INT NOT NULL,                -- Quantity of products sold
    totalPrice DECIMAL(10, 2) NOT NULL,   -- Total price for the sale
    saleDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp for when the sale occurred
);

INSERT INTO Users (name, email, password, role, createdAt, updatedAt) VALUES
('John Doe', 'john.doe@example.com', 'password123', 'admin', '2023-05-01 12:00:00', '2023-05-01 12:00:00'),
('Jane Smith', 'jane.smith@example.com', 'password123', 'user', '2023-06-15 12:01:00', '2023-06-15 12:01:00'),
('Alice Brown', 'alice.brown@example.com', 'password123', 'user', '2023-07-10 12:02:00', '2023-07-10 12:02:00'),
('Bob Johnson', 'bob.johnson@example.com', 'password123', 'guest', '2023-08-20 12:03:00', '2023-08-20 12:03:00'),
('Charlie White', 'charlie.white@example.com', 'password123', 'user', '2023-09-05 12:04:00', '2023-09-05 12:04:00'),
('Daisy Green', 'daisy.green@example.com', 'password123', 'user', '2023-10-01 12:05:00', '2023-10-01 12:05:00'),
('Edward Black', 'edward.black@example.com', 'password123', 'admin', '2023-11-10 12:06:00', '2023-11-10 12:06:00'),
('Fiona Gray', 'fiona.gray@example.com', 'password123', 'guest', '2023-12-01 12:07:00', '2023-12-01 12:07:00'),
('George Blue', 'george.blue@example.com', 'password123', 'user', '2023-12-03 12:08:00', '2023-12-03 12:08:00'),
('Hannah Yellow', 'hannah.yellow@example.com', 'password123', 'user', '2023-12-05 12:09:00', '2023-12-05 12:09:00'),
('Ian Orange', 'ian.orange@example.com', 'password123', 'user', '2023-12-07 12:10:00', '2023-12-07 12:10:00'),
('Jenny Red', 'jenny.red@example.com', 'password123', 'guest', '2023-12-09 12:11:00', '2023-12-09 12:11:00'),
('Kyle Brown', 'kyle.brown@example.com', 'password123', 'admin', '2023-11-15 12:12:00', '2023-11-15 12:12:00'),
('Lara Purple', 'lara.purple@example.com', 'password123', 'user', '2023-10-20 12:13:00', '2023-10-20 12:13:00'),
('Mike Silver', 'mike.silver@example.com', 'password123', 'user', '2023-09-25 12:14:00', '2023-09-25 12:14:00'),
('Nina Gold', 'nina.gold@example.com', 'password123', 'user', '2023-08-10 12:15:00', '2023-08-10 12:15:00'),
('Oscar Pink', 'oscar.pink@example.com', 'password123', 'guest', '2023-07-15 12:16:00', '2023-07-15 12:16:00'),
('Paula Lime', 'paula.lime@example.com', 'password123', 'user', '2023-06-01 12:17:00', '2023-06-01 12:17:00'),
('Quinn Cyan', 'quinn.cyan@example.com', 'password123', 'user', '2023-05-10 12:18:00', '2023-05-10 12:18:00'),
('Rachel Teal', 'rachel.teal@example.com', 'password123', 'user', '2023-04-20 12:19:00', '2023-04-20 12:19:00');

INSERT INTO Addresses (street, city, state, postalCode, country, createdAt, updatedAt) VALUES
('123 Maple St', 'Springfield', 'IL', '62701', 'USA', '2023-05-01 12:00:00', '2023-05-01 12:00:00'),
('456 Oak St', 'Springfield', 'IL', '62701', 'USA', '2023-06-15 12:01:00', '2023-06-15 12:01:00'),
('789 Pine St', 'Chicago', 'IL', '60601', 'USA', '2023-07-10 12:02:00', '2023-07-10 12:02:00'),
('321 Birch St', 'New York', 'NY', '10001', 'USA', '2023-08-20 12:03:00', '2023-08-20 12:03:00'),
('654 Cedar St', 'Los Angeles', 'CA', '90001', 'USA', '2023-09-05 12:04:00', '2023-09-05 12:04:00'),
('987 Elm St', 'Houston', 'TX', '77001', 'USA', '2023-10-01 12:05:00', '2023-10-01 12:05:00'),
('111 Walnut St', 'Phoenix', 'AZ', '85001', 'USA', '2023-11-10 12:06:00', '2023-11-10 12:06:00'),
('222 Ash St', 'Philadelphia', 'PA', '19101', 'USA', '2023-12-01 12:07:00', '2023-12-01 12:07:00'),
('333 Beech St', 'San Antonio', 'TX', '78201', 'USA', '2023-12-03 12:08:00', '2023-12-03 12:08:00'),
('444 Cherry St', 'Dallas', 'TX', '75201', 'USA', '2023-12-05 12:09:00', '2023-12-05 12:09:00'),
('555 Fir St', 'San Diego', 'CA', '92101', 'USA', '2023-12-07 12:10:00', '2023-12-07 12:10:00'),
('666 Hemlock St', 'San Jose', 'CA', '95101', 'USA', '2023-12-09 12:11:00', '2023-12-09 12:11:00'),
('777 Holly St', 'Austin', 'TX', '73301', 'USA', '2023-12-11 12:12:00', '2023-12-11 12:12:00'),
('888 Ivy St', 'Jacksonville', 'FL', '32099', 'USA', '2023-12-13 12:13:00', '2023-12-13 12:13:00'),
('999 Juniper St', 'Columbus', 'OH', '43085', 'USA', '2023-12-15 12:14:00', '2023-12-15 12:14:00'),
('101 Sycamore St', 'Charlotte', 'NC', '28201', 'USA', '2023-12-17 12:15:00', '2023-12-17 12:15:00'),
('102 Maplewood St', 'Detroit', 'MI', '48201', 'USA', '2023-12-19 12:16:00', '2023-12-19 12:16:00'),
('103 Magnolia St', 'El Paso', 'TX', '79901', 'USA', '2023-12-21 12:17:00', '2023-12-21 12:17:00'),
('104 Linden St', 'Boston', 'MA', '02108', 'USA', '2023-12-23 12:18:00', '2023-12-23 12:18:00'),
('105 Palm St', 'Denver', 'CO', '80201', 'USA', '2023-12-25 12:19:00', '2023-12-25 12:19:00');


INSERT INTO UserAddress (userId, addressId) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15),
(16, 16), (17, 17), (18, 18), (19, 19), (20, 20);

INSERT INTO Shops (name, ownerId, description, address, phoneNumber, email, createdAt, updatedAt) VALUES
('John’s Electronics', 1, 'A place for all your electronic needs', '123 Maple St, Springfield, IL, 62701', '123-456-7890', 'johns.electronics@example.com', '2023-05-01 12:00:00', '2023-05-01 12:00:00'),
('Jane’s Fashion', 2, 'Trendy clothing for all occasions', '456 Oak St, Springfield, IL, 62701', '123-456-7891', 'janes.fashion@example.com', '2023-06-15 12:01:00', '2023-06-15 12:01:00'),
('Bob’s Books', 3, 'A haven for book lovers', '789 Pine St, Chicago, IL, 60601', '123-456-7892', 'bobs.books@example.com', '2023-07-10 12:02:00', '2023-07-10 12:02:00'),
('The Gadget Shop', 4, 'Innovative gadgets and accessories', '321 Birch St, New York, NY, 10001', '123-456-7893', 'gadget.shop@example.com', '2023-08-20 12:03:00', '2023-08-20 12:03:00'),
('Gourmet Delights', 5, 'Fine dining products and kitchenware', '654 Cedar St, Los Angeles, CA, 90001', '123-456-7894', 'gourmet.delights@example.com', '2023-09-05 12:04:00', '2023-09-05 12:04:00'),
('Furniture World', 6, 'Affordable and quality furniture', '987 Elm St, Houston, TX, 77001', '123-456-7895', 'furniture.world@example.com', '2023-10-01 12:05:00', '2023-10-01 12:05:00'),
('Tech Hub', 7, 'Cutting-edge technology products', '111 Walnut St, Phoenix, AZ, 85001', '123-456-7896', 'tech.hub@example.com', '2023-11-10 12:06:00', '2023-11-10 12:06:00'),
('Home Decor Haven', 8, 'Beautiful home decor items for every room', '222 Ash St, Philadelphia, PA, 19101', '123-456-7897', 'home.decor@example.com', '2023-12-01 12:07:00', '2023-12-01 12:07:00'),
('The Toy Box', 9, 'Toys and games for children of all ages', '333 Beech St, San Antonio, TX, 78201', '123-456-7898', 'toy.box@example.com', '2023-12-03 12:08:00', '2023-12-03 12:08:00'),
('Outdoor Adventures', 10, 'Outdoor gear for the adventurous soul', '444 Cherry St, Dallas, TX, 75201', '123-456-7899', 'outdoor.adventures@example.com', '2023-12-05 12:09:00', '2023-12-05 12:09:00'),
('Pet Paradise', 11, 'Everything for your furry friends', '555 Fir St, San Diego, CA, 92101', '123-456-7900', 'pet.paradise@example.com', '2023-12-07 12:10:00', '2023-12-07 12:10:00'),
('Green Thumb', 12, 'Gardening supplies for plant lovers', '666 Hemlock St, San Jose, CA, 95101', '123-456-7901', 'green.thumb@example.com', '2023-12-09 12:11:00', '2023-12-09 12:11:00'),
('Cycle City', 13, 'Bicycles and accessories for all riders', '777 Holly St, Austin, TX, 73301', '123-456-7902', 'cycle.city@example.com', '2023-12-11 12:12:00', '2023-12-11 12:12:00'),
('The Art Studio', 14, 'Art supplies and painting materials', '888 Ivy St, Jacksonville, FL, 32099', '123-456-7903', 'art.studio@example.com', '2023-12-13 12:13:00', '2023-12-13 12:13:00'),
('Beauty Essentials', 15, 'Beauty and skincare products for everyone', '999 Juniper St, Columbus, OH, 43085', '123-456-7904', 'beauty.essentials@example.com', '2023-12-15 12:14:00', '2023-12-15 12:14:00'),
('Vintage Treasures', 16, 'Vintage and antique items for collectors', '101 Sycamore St, Charlotte, NC, 28201', '123-456-7905', 'vintage.treasures@example.com', '2023-12-17 12:15:00', '2023-12-17 12:15:00'),
('The Coffee Corner', 17, 'Gourmet coffee beans and brewing equipment', '102 Maplewood St, Detroit, MI, 48201', '123-456-7906', 'coffee.corner@example.com', '2023-12-19 12:16:00', '2023-12-19 12:16:00'),
('Crafty Creations', 18, 'Craft supplies for all types of projects', '103 Magnolia St, El Paso, TX, 79901', '123-456-7907', 'crafty.creations@example.com', '2023-12-21 12:17:00', '2023-12-21 12:17:00'),
('Baker’s Delight', 19, 'Baking supplies and ingredients for all bakers', '104 Linden St, Boston, MA, 02108', '123-456-7908', 'bakers.delight@example.com', '2023-12-23 12:18:00', '2023-12-23 12:18:00'),
('Fitness Plus', 20, 'Fitness equipment and supplements', '105 Palm St, Denver, CO, 80201', '123-456-7909', 'fitness.plus@example.com', '2023-12-25 12:19:00', '2023-12-25 12:19:00');

INSERT INTO userShop (userId, shopId) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10),
(11, 11),
(12, 12),
(13, 13),
(14, 14),
(15, 15),
(16, 16),
(17, 17),
(18, 18),
(19, 19),
(20, 20);

INSERT INTO Products (shopId, name, description, price, stock, category, createdAt, updatedAt) VALUES
(1, 'Smartphone', 'Latest model of smartphone with advanced features.', 699.99, 50, 'Electronics', '2023-05-01 12:00:00', '2023-05-01 12:00:00'),
(2, 'Fashionable Jacket', 'Stylish jacket for all seasons.', 129.99, 30, 'Clothing', '2023-06-15 12:01:00', '2023-06-15 12:01:00'),
(3, 'Best-Selling Novel', 'An international bestseller by renowned author.', 19.99, 100, 'Books', '2023-07-10 12:02:00', '2023-07-10 12:02:00'),
(4, 'Wireless Headphones', 'Noise-canceling headphones for an immersive audio experience.', 149.99, 40, 'Electronics', '2023-08-20 12:03:00', '2023-08-20 12:03:00'),
(5, 'Cooking Set', 'Complete kitchenware set for gourmet cooking.', 199.99, 20, 'Kitchen', '2023-09-05 12:04:00', '2023-09-05 12:04:00'),
(6, 'Leather Sofa', 'Comfortable and elegant leather sofa for living rooms.', 899.99, 10, 'Furniture', '2023-10-01 12:05:00', '2023-10-01 12:05:00'),
(7, 'Laptop', 'High-performance laptop for work and entertainment.', 999.99, 25, 'Electronics', '2023-11-10 12:06:00', '2023-11-10 12:06:00'),
(8, 'Decorative Vase', 'Handcrafted vase for modern home decor.', 49.99, 50, 'Home Decor', '2023-12-01 12:07:00', '2023-12-01 12:07:00'),
(9, 'Toy Car Set', 'A fun toy car set for children to enjoy.', 29.99, 75, 'Toys', '2023-12-03 12:08:00', '2023-12-03 12:08:00'),
(10, 'Camping Tent', 'Durable and waterproof tent for outdoor adventures.', 249.99, 15, 'Outdoor Gear', '2023-12-05 12:09:00', '2023-12-05 12:09:00'),
(11, 'Pet Bed', 'Comfortable and cozy bed for your pets.', 39.99, 60, 'Pets', '2023-12-07 12:10:00', '2023-12-07 12:10:00'),
(12, 'Flower Pots', 'Decorative pots for your indoor plants.', 14.99, 80, 'Gardening', '2023-12-09 12:11:00', '2023-12-09 12:11:00'),
(13, 'Mountain Bike', 'Durable mountain bike for rough terrains.', 399.99, 25, 'Sports', '2023-12-11 12:12:00', '2023-12-11 12:12:00'),
(14, 'Oil Paint Set', 'High-quality oil paint set for artists.', 69.99, 30, 'Art Supplies', '2023-12-13 12:13:00', '2023-12-13 12:13:00'),
(15, 'Skincare Kit', 'Complete skincare set for a glowing complexion.', 89.99, 40, 'Beauty', '2023-12-15 12:14:00', '2023-12-15 12:14:00'),
(16, 'Vintage Lamp', 'Antique-style lamp for a nostalgic feel.', 59.99, 15, 'Home Decor', '2023-12-17 12:15:00', '2023-12-17 12:15:00'),
(17, 'Coffee Maker', 'High-quality coffee machine for home use.', 119.99, 50, 'Kitchen', '2023-12-19 12:16:00', '2023-12-19 12:16:00'),
(18, 'Crafting Supplies', 'Complete set of tools for all crafting projects.', 34.99, 100, 'Crafts', '2023-12-21 12:17:00', '2023-12-21 12:17:00'),
(19, 'Baking Set', 'Complete baking set for enthusiasts.', 79.99, 40, 'Baking', '2023-12-23 12:18:00', '2023-12-23 12:18:00'),
(20, 'Treadmill', 'High-quality treadmill for home fitness.', 499.99, 10, 'Fitness', '2023-12-25 12:19:00', '2023-12-25 12:19:00');


INSERT INTO Sales (productId, userId, quantity, totalPrice, saleDate) VALUES
(1, 1, 2, 1399.98, '2023-05-02 14:00:00'),
(2, 2, 1, 129.99, '2023-06-16 15:00:00'),
(3, 3, 3, 59.97, '2023-07-11 16:00:00'),
(4, 4, 1, 149.99, '2023-08-21 17:00:00'),
(5, 5, 1, 199.99, '2023-09-06 18:00:00'),
(6, 6, 1, 899.99, '2023-10-02 19:00:00'),
(7, 7, 2, 1999.98, '2023-11-11 20:00:00'),
(8, 8, 5, 249.95, '2023-12-02 21:00:00'),
(9, 9, 4, 119.96, '2023-12-04 22:00:00'),
(10, 10, 1, 249.99, '2023-12-06 23:00:00'),
(11, 11, 2, 79.98, '2023-12-08 14:30:00'),
(12, 12, 3, 44.97, '2023-12-10 15:30:00'),
(13, 13, 1, 399.99, '2023-12-12 16:30:00'),
(14, 14, 1, 69.99, '2023-12-14 17:30:00'),
(15, 15, 2, 179.98, '2023-12-16 18:30:00'),
(16, 16, 1, 59.99, '2023-12-18 19:30:00'),
(17, 17, 3, 359.97, '2023-12-20 20:30:00'),
(18, 18, 5, 174.95, '2023-12-22 21:30:00'),
(19, 19, 2, 159.98, '2023-12-24 22:30:00'),
(20, 20, 1, 499.99, '2023-12-26 23:30:00');
