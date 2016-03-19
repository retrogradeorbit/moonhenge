CSS=build/css/style.css
APP=build/js/compiled/moonhenge.js
IDX=build/index.html
IMG=build/img/sprites.png
IMG_PUBLIC=$(subst build,resources/public,$(IMG))
SFX_SOURCE=$(wildcard resources/public/sfx/*.ogg)
SFX=$(subst resources/public,build,$(SFX_SOURCE))
MUSIC_SOURCE=$(wildcard resources/public/music/*.ogg)
MUSIC=$(subst resources/public,build,$(MUSIC_SOURCE))
ME=$(shell basename $(shell pwd))
REPO=git@github.com:retrogradeorbit/moonhenge.git

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

clean:
	lein clean
	rm -rf $(CSS) $(APP) $(IDX) $(IMG) $(SFX) $(MUSIC)

test-server: all
	cd build && python -m SimpleHTTPServer

setup-build-folder:
	git clone $(REPO) build/
	cd build && git checkout gh-pages
