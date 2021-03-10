FROM node:15
WORKDIR /usr/app
COPY ./gladio .
RUN npm install
EXPOSE 4000
CMD ["npm", "start"]