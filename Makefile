server: 
	@$(MAKE) -C src/ server

client: 
	@$(MAKE) -C src/ client

clean:
	@$(MAKE) -C src/ clean

.PHONY: server client clean
