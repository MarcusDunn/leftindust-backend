let {url} = require("../src/server");

let port = 5000;
let testUrl = url.replace(RegExp(":\d+"), `${port}`);

describe("config is valid", () => {
    test("port is number", async () => {
        expect(typeof port).toBe("number")
    })

    test("testUrl is string", async () => {
        expect(typeof testUrl).toBe("string")
    })
})


module.exports = {
    port,
    url: testUrl,
}
