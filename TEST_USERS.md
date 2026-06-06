# Demo accounts (h2 profile only)
#
# Created on first boot of the backend with SPRING_PROFILES_ACTIVE=h2 by
# DataSeeder.java. Each is a regular user with the role USER. H2 is
# in-memory, so accounts reset every time the backend restarts.
#
# Login identifier can be either the email or the username.
#
# | username | email                | password         | phone        |
# |----------|----------------------|------------------|--------------|
# | alice    | alice@example.com    | Alice1234!@#$    | +48501111111 |
# | bob      | bob@example.com      | Bob12345!@#$     | +48502222222 |
# | charlie  | charlie@example.com  | Charlie1!@#$     | +48503333333 |
# | diana    | diana@example.com    | Diana1234!@#$    | +48504444444 |
#
# Quick login example:
#   curl -X POST http://localhost:8080/api/v1/tokens \
#        -H "Content-Type: application/json" \
#        -d '{"identifier":"alice","password":"Alice1234!@#$"}'
#
# These are NOT seeded in the default (Supabase) profile — that database
# is untouched.
