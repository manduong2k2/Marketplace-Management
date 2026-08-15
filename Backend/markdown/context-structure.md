Structure: MVC Architecture

Layers:
- Controller: Handles HTTP requests and responses
- Service: Contains business logic
- Repository: Handles data persistence
- Model: Represents data structures
- DTO: Data Transfer Objects
- Entity: Domain entities
- Enum: Enumerations
- Exception: Custom exceptions
- Config: Configuration files
- Constants: Constants
- Contracts: Interfaces for services and repositories, can be used for dependency injection, should not be modified, only extended
- Validators: Validation logic
- Mappers: Mapping logic, convert between entities and DTOs, must implement IMapper interface, it's a generic contract for mapping, can be used for dependency injection
- Security: Security policies and rules to handle authorization
- Events: Event class contain what happened, to what, when and who did it
- Consumers: Event consumers, just like controllers but asynchronously, take request from events not HTTP requests. 

Flows:
- Controllers receive HTTP requests, call Security to authorize current user, call Validators to validate request, map request to command, call services
- Services receive commands, execute business logic, build models from command and call repositories
- Repositories receive models, map to entities, persist data and return data to services
- Services fire events when something happens, consumers at other contexts receive events and process them
- Services build responses from models and return to controllers
- Controllers receive responses, process protocol metadata and return HTTP responses