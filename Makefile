# Variables
SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = App

# Regla por defecto (se ejecuta al escribir solo 'make')
all: run

# Compilar el código
compile:
	@echo "Compilando código..."
	@mkdir -p $(BIN_DIR)
	javac $(SRC_DIR)/*.java -d $(BIN_DIR)

# Ejecutar la aplicación
run: compile
	@echo "Ejecutando..."
	@echo "----------------------------------------"
	@java -cp $(BIN_DIR) $(MAIN_CLASS)

# Limpiar archivos compilados
clean:
	@echo "Limpiando compilados..."
	@rm -rf $(BIN_DIR)