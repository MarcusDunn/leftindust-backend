describe("general smoke tests for gladio", () => {
    const {port, url} = require("./testConfig")
    const {request, gql} = require("graphql-request")

    beforeAll(async () => {
        await require("../src/server").server.listen({port})
    })

    test("jest working", async () => {
        expect(true).toBe(true)
    })

    test("server starts", async () => {

        const {mockingbirdIsAlive} = await request(url, gql`{mockingbirdIsAlive}`)
        expect(mockingbirdIsAlive).toBe(true)
    })
})