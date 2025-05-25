const { faker } = require("@faker-js/faker");

function randomEmail() {
  return faker.internet.email();
}

function randomFirstName() {
  return faker.person.firstName();
}

function randomLastName() {
  return faker.person.lastName();
}

function randomFullName() {
  return faker.person.fullName();
}

function randomInt(min = 0, max = 1000) {
  return faker.number.int({ min, max });
}

function randomFloat(min = 0, max = 1000, precision = 2) {
  return parseFloat(
    faker.number
      .float({ min, max, precision: Math.pow(10, -precision) })
      .toFixed(precision),
  );
}

function randomDouble(min = 0, max = 1000, precision = 4) {
  return parseFloat(
    faker.number
      .float({ min, max, precision: Math.pow(10, -precision) })
      .toFixed(precision),
  );
}

function randomContactNumber() {
  return faker.phone.number("+91-##########"); // Custom for Indian format; modify as needed
}

function randomPassword(length = 12) {
  return faker.internet.password({ length, memorable: false });
}

function randomATMCardNumber() {
  return faker.finance.creditCardNumber("6221##############"); // Random BIN pattern
}

function randomCreditCardNumber() {
  return faker.finance.creditCardNumber();
}

function randomDate(start = new Date(2000, 0, 1), end = new Date()) {
  return faker.date.between({ from: start, to: end });
}

function randomUUID() {
  return faker.string.uuid();
}

function randomBoolean() {
  return faker.datatype.boolean();
}

function randomAddress() {
  return faker.location.streetAddress(true);
}

function randomCity() {
  return faker.location.city();
}

function randomCountry() {
  return faker.location.country();
}

function randomZipCode() {
  return faker.location.zipCode();
}

