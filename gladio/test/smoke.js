describe("smoke tests for gladio", () => {
    const {request, gql} = require("graphql-request")
    const {server, url} = require("../src/server")

    beforeEach(async () => {
        await server.listen()
    })

    test("jest working", async () => {
        expect(true).toBe(true)
    })

    test("server starts", async () => {

        const {mockingbirdIsAlive} = await request(url, gql`
            {
                mockingbirdIsAlive
            }
        `)

        expect(mockingbirdIsAlive).toBe(true)
    })

    afterEach(async () => {
        await server.stop()
    })
})