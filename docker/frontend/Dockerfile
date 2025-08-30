# 构建阶段
FROM node:18 AS build
WORKDIR /app
RUN git clone https://github.com/Bannirui/mms-web.git . \
    && npm install \
    && npm run build:prod

# 运行阶段
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
