CSS=build/css/style.css
APP=build/js/compiled/moonhenge.js
IDX=build/index.html
ME=$(shell basename $(shell pwd))

all: $(APP) $(CSS) $(IDX)

$(CSS): resources/public/css/style.css
	mkdir -p $(dir $(CSS))
	cp $< $@

$(APP): src/**/** project.clj
	rm -f $(APP)
	lein cljsbuild once min

$(IDX): resources/public/index.html
	cp $< $@

clean:
	lein clean
	rm -rf $(CSS) $(APP) $(IDX)

resources/public/img/sprites.png: resources/img/sprites.png
	-mkdir resources/public/img
	convert resources/img/sprites.png -alpha On -transparent '#010203' resources/public/img/sprites.png

images: resources/public/img/sprites.png
