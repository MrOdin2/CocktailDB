# CocktailDB Documentation Index

Welcome to the CocktailDB documentation! This guide will help you find the right documentation for your needs.

## 📚 Documentation Overview

### For End Users

#### Getting Started
- **[README.md](../README.md)** - Start here! Overview, features, and quick start guide
  - Installation and deployment instructions
  - Usage mode descriptions (Visitor, Barkeeper, Admin)
  - Technology stack overview

#### Security & Authentication
- **[SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)** - Complete security design document
  - Three access modes explained in detail
  - Navigation flows and user journeys
  - Session management and authentication
  - Random cocktail feature documentation
  - Security considerations and limitations

- **[security-quick-reference.md](security-quick-reference.md)** - Quick reference guide
  - Default credentials (change these!)
  - Password hash generation
  - Quick setup steps
  - Troubleshooting common issues
  - Access permissions table

- **[authentication-guide.md](authentication-guide.md)** - Implementation and usage guide
  - Step-by-step authentication setup
  - API endpoint documentation
  - Usage examples for all three modes
  - Frontend and backend integration
  - Testing instructions

#### Network Configuration
- **[local-network-testing.md](local-network-testing.md)** - Access from multiple devices
  - Setting up for local network access
  - Firewall configuration
  - Testing from phones, tablets, and other computers
  - Troubleshooting network issues

- **[local-network-configuration-summary.md](local-network-configuration-summary.md)** - Quick network setup summary

### For Developers & Contributors

#### Architecture & Design
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Complete architecture guide
  - System architecture diagrams
  - Component organization
  - Authentication & authorization flow
  - Data model and API design
  - Frontend patterns and best practices
  - Backend patterns and security
  - Deployment architecture
  - Performance considerations
  - Future improvements

#### API Documentation
- **[OPENAPI.md](OPENAPI.md)** - OpenAPI 3.0 specification documentation
  - Interactive Swagger UI access
  - Complete API reference
  - Authentication and security schemes
  - Request/response examples
  - Data models and schemas
  - Testing API endpoints
  - Best practices for adding new endpoints

#### Development Guidelines
- **[../.github/copilot-instructions.md](../.github/copilot-instructions.md)** - Repository conventions
  - Coding standards and style guides
  - Component design principles
  - Responsive design requirements
  - Testing guidelines

- **[SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)** - Contributor guidance section
  - Adding new features to each mode
  - Security best practices
  - Testing access control
  - Code review checklist
  - Common pitfalls to avoid

#### Deployment
- **[docker-deploy-guide](docker-deploy-guide)** - Docker deployment quick reference
  - Build and start commands
  - Stop commands

## 🎯 Quick Start Paths

### I want to use CocktailDB as a visitor (no login)
1. Read [README.md](../README.md) - Getting Started section
2. Deploy using Docker or run locally
3. Navigate to `http://localhost/` - you're ready to browse!

### I want to set up barkeeper/admin access
1. Read [README.md](../README.md) - Getting Started section
2. Follow [security-quick-reference.md](security-quick-reference.md) - Quick Setup
3. Generate password hashes and configure `.env` file
4. Deploy and test login

### I want to access from my phone/tablet
1. Set up the application following [README.md](../README.md)
2. Follow [local-network-testing.md](local-network-testing.md) for network configuration
3. Find your server's IP address
4. Access from any device on your network

### I want to understand the security model
1. Read [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Overview section
2. Review the three access modes and their purposes
3. See navigation flows and user journeys
4. Understand authentication and session management

### I want to use the API or integrate with CocktailDB
1. Start the application following [README.md](../README.md)
2. Access Swagger UI at `http://localhost:8080/swagger-ui.html`
3. Read [OPENAPI.md](OPENAPI.md) for API documentation and examples
4. Test endpoints directly in Swagger UI or use the OpenAPI spec

### I want to contribute to the project
1. Read [ARCHITECTURE.md](ARCHITECTURE.md) - Full architecture overview
2. Review [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Contributor guidance
3. Check [../.github/copilot-instructions.md](../.github/copilot-instructions.md) - Coding standards
4. Follow development patterns and best practices

### I need to troubleshoot an issue
1. Check [security-quick-reference.md](security-quick-reference.md) - Troubleshooting section
2. Review [authentication-guide.md](authentication-guide.md) - Testing section
3. For network issues: [local-network-testing.md](local-network-testing.md)
4. For architecture questions: [ARCHITECTURE.md](ARCHITECTURE.md) - Monitoring & Troubleshooting

## 📖 Document Summaries

### Main Documentation

| Document | Purpose | Audience |
|----------|---------|----------|
| README.md | Project overview and getting started | Everyone |
| SECURITY_CONCEPT.md | Security design and access control | Users & Developers |
| ARCHITECTURE.md | System architecture and development | Developers |

### Quick References

| Document | Purpose | Audience |
|----------|---------|----------|
| security-quick-reference.md | Quick security setup and commands | Users |
| authentication-guide.md | Detailed auth setup and API docs | Users & Developers |
| docker-deploy-guide | Docker deployment commands | Users |

### Specialized Guides

| Document | Purpose | Audience |
|----------|---------|----------|
| OPENAPI.md | API documentation and OpenAPI spec | Developers & API Users |
| local-network-testing.md | Multi-device network setup | Users |
| local-network-configuration-summary.md | Network setup summary | Users |
| copilot-instructions.md | Repository conventions | Developers |

## 🔍 Topic Index

### Access Control & Security
- Three access modes: [README.md](../README.md), [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Authentication setup: [security-quick-reference.md](security-quick-reference.md), [authentication-guide.md](authentication-guide.md)
- Password management: [security-quick-reference.md](security-quick-reference.md)
- Session management: [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Security limitations: [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)

### Features & Usage
- Visitor mode: [README.md](../README.md), [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Barkeeper mode: [README.md](../README.md), [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Admin mode: [README.md](../README.md), [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Random cocktail picker: [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- Navigation flows: [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)

### Development
- Architecture overview: [ARCHITECTURE.md](ARCHITECTURE.md)
- Component organization: [ARCHITECTURE.md](ARCHITECTURE.md)
- API documentation: [OPENAPI.md](OPENAPI.md), [authentication-guide.md](authentication-guide.md)
- API design: [ARCHITECTURE.md](ARCHITECTURE.md), [OPENAPI.md](OPENAPI.md)
- Frontend patterns: [ARCHITECTURE.md](ARCHITECTURE.md)
- Backend patterns: [ARCHITECTURE.md](ARCHITECTURE.md)
- Testing: [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md), [authentication-guide.md](authentication-guide.md)

### Deployment
- Docker setup: [README.md](../README.md), [docker-deploy-guide](docker-deploy-guide)
- Local network: [local-network-testing.md](local-network-testing.md)
- Environment configuration: [security-quick-reference.md](security-quick-reference.md)
- Production considerations: [ARCHITECTURE.md](ARCHITECTURE.md)

## 💡 Frequently Asked Questions

### How do I change the default passwords?
See [security-quick-reference.md](security-quick-reference.md) - Quick Setup section

### How do I access CocktailDB from my phone?
See [local-network-testing.md](local-network-testing.md)

### What's the difference between visitor, barkeeper, and admin modes?
See [README.md](../README.md) - Usage Modes section and [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Overview

### How do I add a new feature?
See [ARCHITECTURE.md](ARCHITECTURE.md) - Adding New Features section and [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Contributor guidance

### How does the random cocktail picker work?
See [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Random Cocktail Feature section

### Where are the API endpoints documented?
See [OPENAPI.md](OPENAPI.md) for complete OpenAPI documentation with interactive Swagger UI, and [authentication-guide.md](authentication-guide.md) for authentication-specific endpoints

## 📝 Contributing to Documentation

If you find errors or want to improve the documentation:
1. Review the existing documentation structure
2. Make changes that maintain consistency
3. Update this index if adding new documents
4. Follow the markdown style used in existing docs
5. Submit a pull request

## 📞 Getting Help

- Check the troubleshooting sections in relevant documents
- Review the FAQ section above
- Check GitHub issues for similar problems
- Create a new issue with detailed information

---

**Last Updated**: November 2024  
**Version**: 1.0  
**Maintained By**: CocktailDB Contributors
