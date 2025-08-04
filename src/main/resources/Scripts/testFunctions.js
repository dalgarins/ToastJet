function isStatusOk(response) {
  console.log("The is status ", typeof response.status, response.status)
  return response.status === 200;
}

function isStatus(response, statusCode) {
  return response.status === statusCode;
}

function isStatusFailed(response) {
  return response.status >= 400;
}

function isClientError(response) {
  return response.status >= 400 && response.status < 500;
}

function isServerError(response) {
  return response.status >= 500;
}

function hasHeader(response, headerName) {
  return response.responseHeaders.hasOwnProperty(headerName);
}

function headerMatches(response, headerName, value) {
  return response.responseHeaders[headerName]?.toLowerCase() === value.toLowerCase();
}

function headersContainValue(response, value) {
  return Object.values(response.responseHeaders)
    .some(v => v.toLowerCase().includes(value.toLowerCase()));
}

function hasCookie(response, name) {
  return response.setCookie.some(cookie => cookie.name.toLowerCase() === name.toLowerCase());
}

function cookieMatches(response, name, value) {
  return response.setCookie.some(cookie =>
    cookie.name.toLowerCase() === name.toLowerCase() && cookie.value === value
  );
}

function cookiesContainValue(response, value) {
  return response.setCookie.some(cookie =>
    cookie.value.toLowerCase().includes(value.toLowerCase())
  );
}

function hasErrors(response) {
  return response.error === true;
}

function hasErrorMessages(response) {
  return response.errorMessage.length > 0;
}

function errorMessageContains(response, keyword) {
  return response.errorMessage.some(msg =>
    msg.toLowerCase().includes(keyword.toLowerCase())
  );
}

function isJsonBody(response) {
  try {
    const trimmed = response.data?.trim();
    return trimmed && (
      (trimmed.startsWith('{') && trimmed.endsWith('}')) ||
      (trimmed.startsWith('[') && trimmed.endsWith(']'))
    );
  } catch {
    return false;
  }
}

function isXmlBody(response) {
  const trimmed = response.data?.trim();
  return trimmed?.startsWith('<') && trimmed?.endsWith('>');
}

function isYamlBody(response) {
  const trimmed = response.data?.trim();
  return trimmed?.includes(': ') && !isJsonBody(response) && !isXmlBody(response);
}

function bodyContains(response, keyword) {
  return response.data?.toLowerCase().includes(keyword.toLowerCase());
}

function bodyMatchesRegex(response, regex) {
  return regex.test(response.data ?? '');
}

function tookLongerThan(response, ms) {
  return response.timeTaken > ms;
}

function sizeExceeds(response, bytes) {
  return response.size > bytes;
}
