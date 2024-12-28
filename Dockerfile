# Stage 1: Build the React app
FROM node:20-slim AS builder

# Update npm to version 11
RUN npm install -g npm@11.0.0

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the React app
RUN npm run build

# Stage 2: Create a lightweight production image
FROM node:20-slim

# Update npm to version 11
RUN npm install -g npm@11.0.0

# Set the working directory
WORKDIR /app

# Copy only the build output from the builder stage
COPY --from=builder /app/build ./build

# Install only production dependencies
COPY package*.json ./
RUN npm install --production

# Expose port 3000
EXPOSE 3000

# Start the server (assumes the server script serves the build output)
CMD ["npm", "start"]

