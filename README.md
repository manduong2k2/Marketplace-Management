# Marketplace Management System

A full-stack Marketplace Management application built with Spring Boot (Backend) and React (Frontend) with Docker Compose for containerized deployment.

## Features

### User Features
- **Authentication**: User registration, login, and profile management
- **Product Browsing**: View products with filtering by category and brand
- **Product Details**: Detailed product information with images
- **Shopping Cart**: Add items, update quantities, remove items
- **Checkout**: Place orders with shipping information
- **Order History**: View past orders with status tracking
- **Order Details**: View detailed order information

### Admin Features
- **Admin Authentication**: Separate admin login system
- **Brand Management**: Create, read, update, and delete brands
- **Category Management**: Create, read, update, and delete categories
- **Product Management**: Create, read, update, and delete products with images
- **Order Management**: View all orders, search, filter by status, and view order details

## Tech Stack

### Backend
- **Java 21**
- **Spring Boot 4.x**
- **Spring Security** (Authentication & Authorization)
- **Spring Data JPA** (Database operations)
- **PostgreSQL** (Database)
- **Redis** (Caching)
- **Lombok** (Code generation)
- **Maven** (Build tool)

### Frontend
- **React 19**
- **Vite** (Build tool)
- **React Router** (Navigation)
- **Axios** (HTTP client)
- **CSS3** (Styling)
- **FontAwesome** (Icons)

### DevOps
- **Docker** (Containerization)
- **Docker Compose** (Multi-container orchestration)
- **Mailpit** (Email testing)

## Prerequisites

- Docker and Docker Compose installed
- Node.js 20+ (for local frontend development)
- Java 21+ (for local backend development)
- Maven 3.8+ (for local backend development)

## Installation

### Using Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone <repository-url>
cd 7-11-Interview-Test
```

2. Start all services:
```bash
docker-compose up -d
```

3. Access the application:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8083
- Mailpit (Email testing): http://localhost:8025

### Local Development

### 3rd Party Services Setup

- PostgreSQL
- Redis
- RabbitMQ
- Mailpit

Make sure to start these services before starting the backend and frontend and make sure backend and frontend have the correct connection configurations.

#### Backend Setup

1. Navigate to the Backend directory:
```bash
cd Backend
```

2. Configure PostgreSQL database connection in `application.properties`

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

#### Frontend Setup

1. Navigate to the Frontend directory:
```bash
cd FrontEnd
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

## Project Structure

```
7-11-Interview-Test/
├── Backend/
│   ├── src/main/java/com/StoreManagement/
│   │   ├── Auth/              # Authentication module
│   │   ├── Catalog/           # Product, Brand, Category management
│   │   ├── Order/             # Order management
│   │   ├── Shared/            # Shared utilities
│   │   └── User/              # User management
│   └── pom.xml
├── FrontEnd/
│   ├── src/
│   │   ├── components/        # Reusable components
│   │   ├── contexts/          # React contexts (Auth, Cart, Admin)
│   │   ├── layouts/           # Layout components
│   │   ├── pages/             # Page components
│   │   ├── routes/            # Routing configuration
│   │   ├── services/          # API services
│   │   └── configs/           # Configuration files
│   └── package.json
├── docker-compose.yml
└── README.md
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user info

### Vendor
- `GET /api/vendors` - List vendors with pagination and filtering
- `GET /api/vendors/{id}` - Get vendor details
- `POST /api/vendors` - Create vendor (Admin)
- `PUT /api/vendors/{id}` - Update vendor (Admin)
- `DELETE /api/vendors/{id}` - Delete vendor (Admin)

### Products
- `GET /api/products` - List products with pagination and filtering
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Categories
- `GET /api/categories` - List all categories
- `POST /api/categories` - Create category (Admin)
- `PUT /api/categories/{id}` - Update category (Admin)
- `DELETE /api/categories/{id}` - Delete category (Admin)

### Brands
- `GET /api/brands` - List all brands
- `POST /api/brands` - Create brand (Admin)
- `PUT /api/brands/{id}` - Update brand (Admin)
- `DELETE /api/brands/{id}` - Delete brand (Admin)

### Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update item quantity
- `DELETE /api/cart/items/{id}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

### Orders
- `POST /api/orders` - Place new order
- `GET /api/orders/me` - Get user's orders
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders` - Get all orders (Admin)

## Docker Services

The `docker-compose.yml` includes the following services:

- **postgres**: PostgreSQL database
- **redis**: Redis cache
- **rabbitmq**: RabbitMQ message broker
- **mailpit**: Email testing service
- **backend**: Spring Boot application
- **frontend**: React application

## Environment Variables

Configure the following environment variables in `.env` file or docker-compose.yml:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - Database configuration
- `REDIS_HOST`, `REDIS_PORT` - Redis configuration
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD` - RabbitMQ configuration
- `JWT_PRIVATE_KEY`, `JWT_PUBLIC_KEY` - JWT token private and public keys
- `API_URL` - Backend API URL for frontend

## Default Credentials

### Admin
- Email: admin@storemanagement.com
- Password: 123456

### User
- Register through the application

## Development

### Adding New Features

1. **Backend**: Create new controllers, services, repositories, domain models, mappers, and entities following the existing structure, make sure to isolate models and entities in their bounded contexts.
2. **Frontend**: Create new pages, components, and services following the existing patterns
3. **API Integration**: Add new service methods in the `services` directory

## License

This project is for demonstration purposes.

## Contact

For questions or support, please contact me.
