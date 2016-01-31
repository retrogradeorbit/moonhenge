CSS=build/css/style.css
APP=build/js/compiled/moonhenge.js
IDX=build/index.html
IMG=build/img/sprites.png build/img/sprites-2.png
IMG_PUBLIC=$(subst build,resources/public,$(IMG))
SFX_SOURCE=$(wildcard resources/public/sfx/*.ogg)
SFX=$(subst resources/public,build,$(SFX_SOURCE))
MUSIC_SOURCE=$(wildcard resources/public/music/*.ogg)
MUSIC=$(subst resources/public,build,$(MUSIC_SOURCE))
ME=$(shell basename $(shell pwd))

all: $(APP) $(CSS) $(IDX) $(IMG) $(SFX) $(MUSIC)

$(CSS): resources/public/css/style.css
	mkdir -p $(dir $(CSS))
	cp $< $@

$(APP): src/**/** project.clj
	rm -f $(APP)
	lein cljsbuild once min

$(IDX): resources/public/index.html
	cp $< $@

$(IMG): $(IMG_PUBLIC)
	mkdir -p build/img/
	cp $? build/img/

$(SFX): $(SFX_SOURCE)
	mkdir -p build/sfx/
	cp $? build/sfx/

$(MUSIC): $(MUSIC_SOURCE)
	mkdir -p build/music/
	cp $? build/music/

resources/public/img/sprites.png: resources/img/sprites.png
	mkdir -p resources/public/img
	convert resources/img/sprites.png -alpha On -transparent '#010203' resources/public/img/sprites.png

resources/public/img/sprites-2.png: resources/img/sprites-2.png
	cp $< $@

images: $(IMG_PUBLIC)

clean:
	lein clean
	rm -rf $(CSS) $(APP) $(IDX) $(IMG) $(SFX) $(MUSIC)
	rm resources/public/img/sprites*.png

