#!/bin/bash
# Helper script to find your local network IP address for accessing CocktailDB from other devices

echo "=========================================="
echo "CocktailDB - Local Network IP Finder"
echo "=========================================="
echo ""

# Detect OS
OS_TYPE=$(uname -s)

echo "Detected OS: $OS_TYPE"
echo ""
echo "Your local network IP address(es):"
echo "------------------------------------------"

case "$OS_TYPE" in
    Linux*)
        # Linux - try different methods
        if command -v ip &> /dev/null; then
            ip addr show | grep "inet " | grep -v "127.0.0.1" | awk '{print $2}' | cut -d/ -f1 | while read -r ip; do
                echo "  $ip"
            done
        elif command -v hostname &> /dev/null; then
            hostname -I | tr ' ' '\n' | grep -v "127.0.0.1" | while read -r ip; do
                echo "  $ip"
            done
        else
            echo "  Could not detect IP. Try: ip addr show"
        fi
        ;;
    Darwin*)
        # macOS
        ifconfig | grep "inet " | grep -v "127.0.0.1" | awk '{print $2}' | while read -r ip; do
            echo "  $ip"
        done
        ;;
    *)
        echo "  Unsupported OS. Please check your network settings manually."
        ;;
esac

echo "------------------------------------------"
echo ""
echo "To access CocktailDB from other devices:"
echo "  1. Make sure Docker Compose is running (docker compose up -d)"
echo "  2. Use one of the IP addresses above"
echo "  3. Open http://[IP_ADDRESS] in a browser on another device"
echo ""
echo "Example: http://192.168.1.100"
echo ""
echo "For more information, see: docs/local-network-testing.md"
echo ""
