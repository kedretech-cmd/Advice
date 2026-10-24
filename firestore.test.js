const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read unpublished advice", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("advice").doc("draft_adv").set({
      title: "Draft Advice",
      advice: "Content",
      dailyAction: "Action",
      category: "ተነሳሽነት",
      published: false,
      publicationDate: "2026-10-24",
      createdAt: new Date(),
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("advice").doc("draft_adv").get());
});

test("Public user: can read published advice", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("advice").doc("pub_adv").set({
      title: "Published Advice",
      advice: "Content",
      dailyAction: "Action",
      category: "ተነሳሽነት",
      published: true,
      publicationDate: "2026-10-24",
      createdAt: new Date(),
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("advice").doc("pub_adv").get());
});

test("Authenticated user: can create reaction for themselves", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("reactions").doc("rx_1").set({
      userId: ALICE_UID,
      adviceId: "pub_adv",
      reactionType: "like",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create reaction as another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("reactions").doc("rx_2").set({
      userId: BOB_UID,
      adviceId: "pub_adv",
      reactionType: "like",
      createdAt: new Date(),
    })
  );
});

test("Admin user: can create advice", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: "kedretech@gmail.com" }).firestore();
  await assertSucceeds(
    adminDb.collection("advice").doc("admin_adv").set({
      title: "Admin Advice Title",
      advice: "Detailed practical wisdom advice in Amharic",
      dailyAction: "Do action today",
      category: "ተነሳሽነት",
      published: true,
      publicationDate: "2026-10-24",
      createdAt: new Date(),
    })
  );
});

test("Non-admin user: cannot create advice", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@test.com" }).firestore();
  await assertFails(
    aliceDb.collection("advice").doc("fake_adv").set({
      title: "Fake Advice",
      advice: "Some text",
      dailyAction: "Action",
      category: "ተነሳሽነት",
      published: true,
      publicationDate: "2026-10-24",
      createdAt: new Date(),
    })
  );
});
