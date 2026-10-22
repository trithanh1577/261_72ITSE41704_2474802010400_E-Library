# Public deployment: Render and Supabase

This project is prepared for a Docker-based Render web service with Supabase
PostgreSQL as its persistent database. These files prepare the application;
the service is not public until a team member connects the repository and
creates it in the Render dashboard.

## 1. Prepare Supabase

1. Create a Supabase project and set a strong database password.
2. In the project dashboard, open **Connect** and choose **Session pooler**.
   This mode uses IPv4 and supports prepared statements, which suits a
   persistent Render web service.
3. Keep the displayed host, username, database name and port available. Use
   the values shown by Supabase rather than guessing the pooler host or user.
   The shared session pooler normally uses port `5432`.

## 2. Connect the repository to Render

1. Push the deployment-ready branch to the team's GitHub repository. Per the
   team's handoff, Ngan3112 should perform the push.
2. In Render, create a **Blueprint** from that repository and select the
   repository root containing `render.yaml`.
3. During Blueprint setup, enter the requested values for the variables marked
   `sync: false`. Do this in Render's dashboard, not in the YAML file.
4. For `DB_URL`, enter a JDBC URL in this form, replacing the host and database
   with the values shown under Supabase's **Session pooler**:

   ```text
   jdbc:postgresql://<POOLER-HOST>:5432/postgres?sslmode=require
   ```

5. Set `DB_USERNAME` to the Supabase session-pooler username (usually
   `postgres.<PROJECT-REF>`), and `DB_PASSWORD` to the database password.
   Set `BOOTSTRAP_ADMIN_USERNAME` to the first librarian's username and
   `BOOTSTRAP_ADMIN_PASSWORD` to a unique password with at least 12 characters.
   The display name is optional.
6. Review the environment values, then create/deploy the Blueprint. Render
   builds the Docker image, starts the app and provides an `onrender.com` URL.

The `prod` profile does not create the local demo users (`admin/admin123` and
`member/member123`). On an empty hosted database it creates only the librarian
whose bootstrap credentials you entered. Remove `BOOTSTRAP_ADMIN_PASSWORD`
from Render after the first successful start; it is only read when the user
table is empty. Member self-registration/account management is not implemented
at this Week 6 checkpoint, so do not advertise member borrowing as available
until member accounts can be provisioned securely.

## 3. Verify the deployment

1. Confirm Render reports the service as live and `/login` responds.
2. Sign in with the bootstrap librarian account, create a book and a copy, and
   confirm they are visible in the catalogue.
3. Restart or redeploy the Render service, then confirm the same records remain.
4. Do not mark the cloud persistence check complete until it has been tested
   against the actual Supabase project.

The Blueprint uses Render's free web-service plan. Free services can spin down
when idle, so the first request after inactivity may be delayed. Supabase and
Render account availability, quotas and pricing can change; check both
dashboards before choosing a plan.

## Credentials and ownership

- Never commit database or bootstrap passwords, `.env` files, Supabase keys or
  a production database dump.
- Do not send these secrets in chat. Enter them directly in Render's secret
  environment-variable form.
- Render needs repository access for deployment. Ngan3112 should push the
  prepared branch, and a team member with Render access must connect it.

References: [Render Web Services](https://render.com/docs/web-services),
[Render Blueprint YAML](https://render.com/docs/blueprint-spec),
[Render environment variables](https://render.com/docs/configure-environment-variables),
[Supabase PostgreSQL connection methods](https://supabase.com/docs/guides/database/connecting-to-postgres).
