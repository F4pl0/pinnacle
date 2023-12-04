# PINNACLE - Portfolio Analysis Platform

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/f4pl0/pinnacle/mavenTest.yml?style=flat-square)
[![codecov](https://codecov.io/gh/F4pl0/pinnacle/graph/badge.svg?token=DXR3RFF0YY)](https://codecov.io/gh/F4pl0/pinnacle)
[![CodeFactor](https://www.codefactor.io/repository/github/f4pl0/pinnacle/badge)](https://www.codefactor.io/repository/github/f4pl0/pinnacle)
[![DeepSource](https://app.deepsource.com/gh/F4pl0/pinnacle.svg/?label=active+issues&show_trend=false&token=Y2jP7YzYepBkldJc3dBU2mul)](https://app.deepsource.com/gh/F4pl0/pinnacle/)
![GitHub license](https://img.shields.io/github/license/f4pl0/pinnacle?style=flat-square)
![GitHub issues](https://img.shields.io/github/issues/f4pl0/pinnacle?style=flat-square)
![GitHub pull requests](https://img.shields.io/github/issues-pr/f4pl0/pinnacle?style=flat-square)
![GitHub last commit](https://img.shields.io/github/last-commit/f4pl0/pinnacle?style=flat-square)

![Pinnacle Logo](.github/assets/pinnacle-logo-cover.png)
Powerful, open-source portfolio analysis platform designed to help users manage and analyze their financial portfolios effectively. This monorepo project consists of a collection of Spring microservices for backend functionality and an Angular frontend for an intuitive user interface. Pinnacle integrates various technologies, including Kafka, Microsoft SQL Server, and Redis, to provide a comprehensive portfolio management and analysis solution.

## Features

Pinnacle offers a range of features to empower users in their financial analysis and portfolio management efforts:

- **OAuth2 Authentication**: Securely access the platform with OAuth2-based authentication, ensuring the safety and privacy of your financial data.

- **Portfolio CRUD**: Create, read, update, and delete portfolios easily, providing complete control over your financial assets.

- **Portfolio Analysis**: Perform in-depth analysis of your portfolio's performance, including historical data and trends.

- **Stock Analysis**: Get detailed information and analysis on individual stocks, helping you make informed investment decisions.

- **Sector Analysis**: Explore the performance of various sectors in the market to assess their potential for investment.

- **Sector and Company News**: Stay updated with the latest news related to sectors and individual companies, ensuring you are always well-informed.

- **Portfolio Optimization Recommendations**: Receive personalized recommendations for optimizing your portfolio based on your financial goals and risk tolerance.

## Requirements

- **A beefy PC** (if you wish to run this locally, this devours RAM worse than chrome) Good luck with this one

- **Java 21** or higher (JDK) [OpenJDK](https://openjdk.org/install/)

- **Maven** [Maven Download](https://maven.apache.org/download.cgi)

- **Docker** (Docker Compose, to run the dependencies such as database, kafka, redis, etc.)

- **Node.js 18 with NPM** or later (For Angular frontend) [Node.js](https://nodejs.org/en/)

- **Angular CLI** (For Angular frontend) [Angular CLI](https://angular.io/cli)


## Getting Started

To get started with Pinnacle, follow these steps:

1. Clone this repository to your local machine.
    ```bash
    git clone https://github.com/F4pl0/pinnacle.git
    ```

2. Make sure you have all the required dependencies installed, see [Requirements](#requirements).

3. Install the frontend dependencies.
    ```bash
    cd pinnacle-frontend
    npm install
    cd ..
    ```

4. Sync Maven dependencies.
    ```bash
    mvn clean install
    ```

5. Run the Docker Compose file to start the dependencies.
    ```bash
    docker-compose up -d
    ```

### Run on Windows
```bash
./run.bat
```

### Run on Linux & Mac
```bash
./run.sh
```

### Enjoy
Navigate to [http://localhost:4200](http://localhost:4200) to access the Pinnacle frontend.

## Troubleshooting & FAQ

Here are some common issues you might encounter while setting up or running Pinnacle, along with their solutions:

1. **Issue: Maven dependencies are not being resolved.**
    - Solution: Make sure you have the correct version of Maven installed and your `settings.xml` file is correctly 
   configured. If the problem persists, try deleting your `.m2` directory and re-running `mvn clean install`.

2. **Issue: Docker Compose services are not starting up.**
    - Solution: Check the Docker Compose logs for any error messages. Make sure you have the correct version of Docker 
   and Docker Compose installed. Also, ensure that the required ports are not being used by other services.

3. **Issue: Frontend is not loading at `http://localhost:4200`.**
    - Solution: Make sure you have run `npm install` in the `pinnacle-frontend` directory and that the Angular server 
   is running. If the problem persists, try clearing your browser cache or opening the application in an incognito 
   window.

4. **Issue: I am getting a `java.lang.OutOfMemoryError`.**
    - Solution: This project requires a significant amount of RAM. If you are running out of memory, try increasing the 
   heap size allocated to the JVM or closing other memory-intensive applications.
    - Try increasing your swap space. [How to increase swap space](https://linuxize.com/post/how-to-add-swap-space-on-ubuntu-20-04/)
    - Run it on a more powerful machine.

5. **Issue: I am getting a `401 Unauthorized` error when trying to access the API.**
    - Solution: Make sure you are passing the correct authentication credentials with your request. If you are using 
   OAuth2, ensure that your access token has not expired, they tend to expire quickly if not refreshed.

Remember, if you encounter an issue that is not listed here, you can always 
[create an issue](https://github.com/f4pl0/pinnacle/issues) on our GitHub repository.

## Contributing

Pinnacle is an open-source project, and we welcome contributions from the community. If you'd like to get involved, please check out our [Contribution Guidelines](CONTRIBUTING.md) for more information.

## License

Pinnacle is released under the [MIT License](LICENSE), which means you are free to use, modify, and distribute it for both personal and commercial purposes.

## Support

If you have any questions or need assistance, please feel free to reach out to our support team by [creating an issue](https://github.com/f4pl0/pinnacle/issues).

---

Thank you for choosing Pinnacle as your go-to portfolio analysis platform. We look forward to helping you make informed financial decisions and optimize your investment strategies. Happy investing!