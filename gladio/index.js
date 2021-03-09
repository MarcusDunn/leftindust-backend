const firebase = require("firebase/app");
require("firebase/auth");

const {RemoteGraphQLDataSource} = require("@apollo/gateway");
const {ApolloServer} = require("apollo-server")
const {ApolloGateway} = require("@apollo/gateway")

const firebaseConfig = {
    apiKey: "AIzaSyDwHAnFFB6ouYkL068TmFubVpa8Mlp8UBE",
    authDomain: "mediq-backend.firebaseapp.com",
    databaseURL: "https://mediq-backend.firebaseio.com",
    projectId: "mediq-backend",
    storageBucket: "mediq-backend.appspot.com",
    messagingSenderId: "710125939146",
    appId: "1:710125939146:web:b4d6cebab35d37d7f5c733"
};

firebase.initializeApp(firebaseConfig)

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
            {name: "mockingbird", url: `${process.env.MOCKINGBIRD_URL}/graphql`},
            // {name: "condor", url: "http://localhost:8081/graphql"},
        ],
    }),
    subscriptions: false
});

server.listen().then(({url}) => {
    console.log(`🚀 Server ready at ${url}`);
});

module.exports = {
    server,
    firebase,
}