# Public deployment: Render and Supabase

The repository contains a Docker-based Render web-service configuration and
uses Supabase PostgreSQL for hosted persistence. Use the steps below to
configure the service and verify a deployment.

## 1. Prepare Supabase

1. Create a Supabase project and set a strong database password.
2. In the project dashboard, open **Connect** and choose **Session pooler**.
   This mode uses IPv4 and supports prepared statements, which suits a
   persistent Render web service.
3. Keep the displayed host, username, database name and port available. Use
   the values shown by Supabase rather than guessing the pooler host or user.
   The shared session pooler normally uses port `5432`.

## 2. Connect the repository to Render

1. Push the reviewed source branch to the team's GitHub repository.
2. In Render, create a **Blueprint** from that repository and select the
   repository root containing `render.yaml`.
3. During Blueprint setup, enter the requested values for the variables marked
   `sync: false`. Do this in Render's dashboard, not in the YAML file.
4. For `DB_URL`, enter a JDBC URL, not the raw `postgresql://` connection URI
   shown by Supabase. For the project values in the supplied screenshot, use:

   ```text
   jdbc:postgresql://<session-pooler-host>:5432/postgres?sslmode=require
   ```

5. Set `DB_USERNAME` and `DB_PASSWORD` to the values shown or configured in
   Supabase. Enter the actual password in `DB_PASSWORD`; do not include it in
   `DB_URL`.
   Set `BOOTSTRAP_ADMIN_USERNAME` to the first librarian's username and
   `BOOTSTRAP_ADMIN_PASSWORD` to a unique password with at least 12 characters
   and no more than 72 UTF-8 bytes (the BCrypt input limit).
   The display name is optional.
6. Review the environment values, then create/deploy the Blueprint. Render
   builds the Docker image, starts the app and provides an `onrender.com` URL.

The `prod` profile does not create the local demo users (`admin/admin123` and
`member/member123`). On an empty hosted database it creates only the librarian
whose bootstrap credentials you entered. Remove `BOOTSTRAP_ADMIN_PASSWORD`
from Render after the first successful start; it is only read when the user
table is empty. Members can register themselves, and librarians can edit member
profiles, membership type and enabled status from the Members page.

## 3. Verify the deployment

1. In Render, verify the service is connected to the intended repository and
   deployment branch.
2. Confirm Render reports the service as live and `/login` responds. If startup
   fails, inspect the first database/driver error in the deploy logs and verify
   `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `DB_DRIVER=org.postgresql.Driver`.
3. Sign in with the bootstrap librarian account, create a book and a copy, and
   confirm they are visible in the catalogue.
4. Restart or redeploy the Render service, then confirm the same records remain.
5. Do not mark the cloud persistence check complete until it has been tested
   against the actual Supabase project.

The application stores records in Supabase PostgreSQL and binds to Render's
`PORT` environment variable (with `8080` as the local default). Review the
Render and Supabase dashboards for service status and plan limits.

## Credentials and ownership

- Never commit database or bootstrap passwords, `.env` files, Supabase keys or
  a production database dump.
- Enter secrets directly in Render's secret environment-variable form.
- If a production password has been shared or captured in a screenshot, rotate
  it. Changing `BOOTSTRAP_ADMIN_PASSWORD` alone does not change an existing
  librarian account's password.
- Render needs repository access for deployment. A team member with Render
  access must connect the repository and branch.

References: [Render Web Services](https://render.com/docs/web-services),
[Render Blueprint YAML](https://render.com/docs/blueprint-spec),
[Render environment variables](https://render.com/docs/configure-environment-variables),
[Supabase PostgreSQL connection methods](https://supabase.com/docs/guides/database/connecting-to-postgres).
