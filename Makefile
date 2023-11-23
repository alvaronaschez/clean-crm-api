run:
	./gradlew bootRun --args='--spring.profiles.active=dev'
test:
	./gradlew test
up:
	docker compose up
down:
	docker compose down
clean:
	./gradlew clean
