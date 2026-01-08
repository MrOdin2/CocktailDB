# Customer QR Code Authentication Guide

## Overview

CocktailDB now includes a customer authentication layer that requires all users to authenticate via a QR code before accessing any part of the application. This feature is designed for establishments (bars, restaurants) that want to control access to their digital cocktail menu.

## How It Works

### Architecture

```
┌─────────────┐
│   Customer  │
│   Scans QR  │
└──────┬──────┘
       │
       │ URL with token: https://your-bar.com/customer-login?token=xxx
       │
       ▼
┌─────────────────────────────────────┐
│  Customer Login Component           │
│  - Validates token with backend     │
│  - Stores token in localStorage     │
│  - Redirects to visitor menu        │
└──────┬──────────────────────────────┘
       │
       │ All subsequent requests include X-Customer-Token header
       │
       ▼
┌─────────────────────────────────────┐
│  Backend Authentication Filter      │
│  - Verifies customer token          │
│  - Checks 24-hour expiration        │
│  - Validates HMAC signature         │
└─────────────────────────────────────┘
```

### Token Structure

Customer tokens are cryptographically signed JWT-like tokens containing:
- **Timestamp**: When the token was generated (Unix epoch)
- **HMAC**: SHA-256 signature using server secret key

Format: `base64(timestamp:hmac_signature)`

The token is valid for **24 hours** from generation time.

## Admin Setup

### 1. Configure Customer Token Secret

Add a secure secret to your `.env` file:

```bash
# Generate a secure random secret
openssl rand -base64 64

# Add to .env
CUSTOMER_TOKEN_SECRET=<your-generated-secret>
```

⚠️ **Important**: Use a strong, unique secret in production. Never commit this to version control.

### 2. Generate QR Code

1. Log in as admin
2. Navigate to **Admin → QR Code**
3. Click "Generate QR Code"
4. The QR code and authentication URL are displayed

### 3. Print or Display QR Code

Options for displaying the QR code:
- **Print**: Click "Print QR Code" button
- **Download**: Right-click QR code image → Save As
- **Digital Display**: Use the authentication URL for digital menus

### 4. Share with Customers

Place the QR code where customers can easily scan it:
- On tables
- At the bar
- On menu cards
- Digital displays

## Customer Experience

### 1. Scan QR Code

Customer scans the QR code with their smartphone camera or QR code app.

### 2. Automatic Authentication

The URL in the QR code automatically:
- Opens the app in their browser
- Authenticates them with the embedded token
- Redirects to the cocktail menu

### 3. Browse Cocktails

Once authenticated, customers can:
- View available cocktails
- Browse by categories
- See detailed recipes
- Use the random picker

### 4. 24-Hour Session

- Authentication lasts 24 hours
- Token stored in browser localStorage
- No need to scan again during this period
- Works even if they close the browser

## Staff Access (Admin/Barkeeper)

### Prerequisites

Before staff can log in, they must also authenticate as a customer:
1. Scan the customer QR code (or visit the auth URL)
2. Once customer authentication is complete, navigate to `/login`
3. Select role (Admin or Barkeeper)
4. Enter password

This two-layer authentication ensures:
- Only authenticated customers can see the staff login page
- Staff accounts have additional security layer
- Separation between customer and staff access

## API Endpoints

### Customer Authentication

#### Generate Token (Admin Only)
```http
GET /api/auth/customer/generate-token
Cookie: sessionId=<admin-session>

Response:
{
  "success": true,
  "token": "MTY3MzAwMDAwMDp...",
  "message": "Token generated successfully"
}
```

#### Authenticate with Token
```http
POST /api/auth/customer/authenticate?token=<token>

Response:
{
  "success": true,
  "token": "MTY3MzAwMDAwMDp...",
  "message": "Customer authentication successful"
}
Set-Cookie: customerToken=<token>; Path=/; Max-Age=86400
```

#### Validate Token
```http
GET /api/auth/customer/validate?token=<token>

Response:
{
  "success": true,
  "message": "Token is valid"
}
```

## Security Features

### Token Security

- **HMAC Signature**: Tokens are cryptographically signed and cannot be forged
- **Timestamp-based**: Each token is unique and tied to generation time
- **Time-limited**: Tokens expire after 24 hours
- **Server-side Validation**: All requests verify token signature and expiration

### Request Authentication

All API requests (except public endpoints) require:
```http
X-Customer-Token: MTY3MzAwMDAwMDp...
```

The HTTP interceptor automatically adds this header to all requests.

### Protected Endpoints

- **Public** (no auth required):
  - `/api/auth/customer/*` - Customer authentication endpoints
  - `/api/docs`, `/swagger-ui` - API documentation
  
- **Customer Auth Required**:
  - All visitor routes
  - Staff login page
  - All data access endpoints
  
- **Staff Auth Required** (Customer + Session):
  - POST/PUT/DELETE on cocktails and ingredients
  - Settings management
  - QR code generation

## Troubleshooting

### Customer Cannot Access Menu

**Issue**: "Customer authentication required" error

**Solutions**:
1. Scan the QR code again
2. Check that JavaScript is enabled
3. Clear browser data and retry
4. Verify the QR code is not expired (should always work, but check server time)

### QR Code Generation Fails

**Issue**: Admin cannot generate QR code

**Solutions**:
1. Verify you're logged in as admin (not barkeeper)
2. Check `CUSTOMER_TOKEN_SECRET` is set in `.env`
3. Restart backend if secret was just added
4. Check backend logs for errors

### Token Expired

**Issue**: Token works initially but stops after some time

**Explanation**: Tokens are valid for 24 hours. After this, customers need to scan the QR code again.

**Solution**: Regenerate QR code with a new token, or have customers scan the existing one again.

### Staff Cannot Login

**Issue**: Staff login page not accessible

**Solution**: 
1. Staff must first authenticate as a customer by scanning the QR code
2. Then navigate to `/login` to enter staff credentials
3. Check that customer token is present in localStorage

### Test Environment

In test environments, customer authentication is disabled to allow automated testing.

To enable/disable:
- Check `spring.profiles.active` property
- Customer auth is skipped when profile is "test"
- Production always requires customer auth

## Configuration Options

### Environment Variables

```bash
# Required: Customer token secret for HMAC signing
CUSTOMER_TOKEN_SECRET=<secure-random-secret>

# Optional: Customize token expiration (not currently configurable, hardcoded to 24h)
# Future enhancement: Make expiration configurable
```

### Token Expiration

Currently hardcoded to 24 hours (86400 seconds). To change:

Edit `CustomerTokenService.kt`:
```kotlin
// Change this value (in seconds)
if (now - timestamp > 86400) return false
```

## Best Practices

### For Establishment Owners

1. **Regular QR Code Rotation**: Generate new QR codes monthly for additional security
2. **Multiple Codes**: Generate separate codes for different areas (tables, bar, entrance)
3. **Physical Security**: Place QR codes where only customers can access them
4. **Backup URL**: Keep a copy of the authentication URL for customers who have issues scanning

### For Developers

1. **Secure Secrets**: Always use strong random secrets for `CUSTOMER_TOKEN_SECRET`
2. **HTTPS Only**: Use HTTPS in production to prevent token interception
3. **Monitor Access**: Log authentication attempts to detect unusual patterns
4. **Token Rotation**: Implement token rotation if a breach is suspected

## Integration Examples

### Custom QR Code Generation

If you need to generate QR codes programmatically (e.g., for printed materials):

```bash
# Generate token via API
curl -X GET https://your-bar.com/api/auth/customer/generate-token \
  -H "Cookie: sessionId=<admin-session>" \
  -o token.json

# Extract token
TOKEN=$(jq -r '.token' token.json)

# Create URL
URL="https://your-bar.com/customer-login?token=$TOKEN"

# Generate QR code with qrencode
qrencode -o qr-code.png "$URL"
```

### Bulk QR Code Generation

For generating multiple codes (e.g., one per table):

```bash
#!/bin/bash
for i in {1..10}; do
  TOKEN=$(curl -s -X GET https://your-bar.com/api/auth/customer/generate-token \
    -H "Cookie: sessionId=$ADMIN_SESSION" | jq -r '.token')
  
  URL="https://your-bar.com/customer-login?token=$TOKEN"
  qrencode -o "table-$i-qr.png" "$URL"
  
  echo "Table $i: $URL"
done
```

## Migration from Previous Version

If upgrading from a version without customer authentication:

1. **Update Environment**: Add `CUSTOMER_TOKEN_SECRET` to `.env`
2. **Generate Initial QR Code**: Create first QR code for customers
3. **Communicate Change**: Inform staff and customers about new QR code requirement
4. **Test Access**: Verify staff can still login after customer authentication
5. **Rollback Plan**: Keep old version running in parallel during transition if needed

## Future Enhancements

Potential improvements for future versions:

- **Configurable Expiration**: Make token lifetime configurable via environment variable
- **Multi-tenant**: Support multiple establishments with separate token secrets
- **Analytics**: Track QR code scans and customer access patterns
- **Token Refresh**: Allow tokens to be refreshed without requiring a new QR code scan
- **Rate Limiting**: Add rate limiting to prevent token generation abuse

## Support

For issues or questions:
- Check backend logs: `docker-compose logs backend`
- Review authentication flow in browser developer tools
- Verify environment variables are set correctly
- Consult the main authentication guide in `docs/authentication-guide.md`
