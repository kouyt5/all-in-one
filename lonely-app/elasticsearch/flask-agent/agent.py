# initialize using environment variables
from elasticapm.contrib.flask import ElasticAPM
from flask import Flask

app = Flask(__name__)
apm = ElasticAPM(app)

# or configure to use ELASTIC_APM in your application's settings
from elasticapm.contrib.flask import ElasticAPM
app.config['ELASTIC_APM'] = {
  # Set required service name. Allowed characters:
  # a-z, A-Z, 0-9, -, _, and space
  'SERVICE_NAME': 'elastic',

  # Use if APM Server requires a token
#   'SECRET_TOKEN': '',

  # Set custom APM Server URL (default: http://localhost:8200)
  'SERVER_URL': 'http://apm:8200',
}

apm = ElasticAPM(app)