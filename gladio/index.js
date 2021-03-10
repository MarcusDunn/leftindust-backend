const server = require("src/server")

server.listen().then(({url}) => {
    console.log(`🚀 Server ready at ${url}`);
});