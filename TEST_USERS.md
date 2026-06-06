# Demo accounts (h2file profile only)
#
# Seeded on first boot of the backend with `--spring.profiles.active=h2file`
# by `DataSeeder.java`. Each is a regular user with the role USER. The
# database is H2 in file mode at `~/xd-app-h2.mv.db`, so accounts
# **persist across restarts** until you delete that file:
#
#   rm ~/xd-app-h2.mv.db*
#
# The seeder is a no-op if the table already has any user, so re-runs
# after manual deletions do not duplicate.
#
# Login identifier can be the username, email, or phone.
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
# Upload a profile pic:
#   curl -X PATCH http://localhost:8080/api/v1/users/1 \
#        -H "Authorization: Bearer <ACCESS_TOKEN>" \
#        -F 'data={"description":"Hi!"};type=application/json' \
#        -F "profilePic=@./photo.png;type=image/png"
#   # response contains `profilePicUrl` like
#   #   http://localhost:8080/api/v1/uploads/1/<uuid>_photo.png
#   # The file is also on disk at
#   #   xd-app-backend/uploads/1/<uuid>_photo.png
#
# These are NOT seeded when running against Supabase — the prod/staging
# database is untouched.
