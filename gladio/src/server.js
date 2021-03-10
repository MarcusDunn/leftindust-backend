const {RemoteGraphQLDataSource} = require("@apollo/gateway");
const {ApolloServer} = require("apollo-server")
const {ApolloGateway} = require("@apollo/gateway")

let url = `${process.env.MOCKINGBIRD_URL || "http://localhost:8080"}/graphql`;

const server = new ApolloServer({
    context: req => req,
    gateway: new ApolloGateway({
        buildService({name, url}) {
            return new RemoteGraphQLDataSource({
                url,
                willSendRequest({request, context}) {
                    let tokenHeaderString = "mediq-auth-token";
                    if (context?.req?.headers[tokenHeaderString]) {
                        request.http.headers.set(tokenHeaderString, context.req.headers[tokenHeaderString])
                    }
                }
            })
        },
        debug: true,
        serviceList: [
            {name: "mockingbird", url: url},
        ],
    }),
    subscriptions: false
});

module.exports = {
    server,
    url,
}