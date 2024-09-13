all:
	mvn package ktlint:check

clean:
	mvn clean

notafter:
	@for CRT in src/test/resources/*crt; do \
		echo "$$CRT"; \
		openssl x509 -in "$$CRT" -text | grep 'Not After'; \
	done
