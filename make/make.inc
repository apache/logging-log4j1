
RMIC:=rmic

# We no longer support documentation creation through GNU-Make.

# ========= Local override ==========
# include site-specific file -- you can override anything above in the
# site-specific file.
-include $(DEPTH)/make/make.loc
#====================================

# Strip leading or trailing spaces
PKG_DIR:=$(strip $(PKG_DIR))

# Substitute '/' to '.' in PKG_DIR
PKG_NAME    := $(subst /,.,$(PKG_DIR))

JOBJECTS := $(JSOURCES:%.java=$(CLASS_DIR)/$(PKG_DIR)/%.class)
JSTUBS   := $(JRMI:%.java=$(CLASS_DIR)/$(PKG_DIR)/%_Stub.class)

# Test sources converted to test objects.
TOBJECTS := $(TSOURCES:%.java=$(CLASS_DIR)/$(PKG_DIR)/%.class)


# RECURSE_TARGETS targets for which we need to call make in subdirs.
RECURSE_TARGETS = lib clean test

all: .PHONY

.PHONY::
	@echo "make lib    - to compile class files"
	@echo "make clean  - to delete previously compiled class files"
	@echo "make rclean - to delete whole class directories!"
	@echo "make build  - compile all .java files in one giant step"
	@echo "make test   - compile test related java files. Requries JUnit."

lib:: $(JOBJECTS) $(JSTUBS)

test::  $(TOBJECTS)

build::
	$(JAVAC) $(JAVAC_FLAGS) -d $(CLASS_DIR)\
          $(shell find . -name "*.java" -not \( -path '*doc-files*' \) )


# Command to run make recursively in subdirectories.  If there are no
# subdirectories this rule is not included.
ifneq ($(strip $(SUBDIRS)),)
$(RECURSE_TARGETS)::
	@@for dir in $(SUBDIRS); do \
	  echo "Making $@ in $$dir"; \
	  $(MAKE) -C $$dir --no-print-directory --no-builtin-rules $@ || exit 1;\
	done
endif


#--------------------------------------------------------------------
# Implicit rules
#--------------------------------------------------------------------

$(CLASS_DIR)/$(PKG_DIR)/%.class : %.java
	$(JAVAC) $(JAVAC_FLAGS) -d $(CLASS_DIR)  $<

# RMI Stub generation
$(CLASS_DIR)/$(PKG_DIR)/%_Stub.class : %.java
	$(RMIC) -d $(CLASS_DIR) $(PKG_NAME).$(patsubst %_Stub.class,%,$(@F))

# -----------------------------------------------------------------
debug ::
	@echo "JOBJECTS = $(JOBJECTS) \n"\
 "CLASS_DIR = $(CLASS_DIR)\n"\
 "PKG_NAME = $(PKG_NAME)\n"\
 "JOBJECTS = $(JOBJECTS)\n"\
 "JSTUBS =$(JSTUBS)\n"\
 
# Do not attempt to remove files if both $(JOBJECTS) and $(JSTUBS) are
# emtpry. This avoids 'rm' complaining about missing files.
clean::
ifneq ($(strip $(JOBJECTS) $(JSTUBS)),)
	rm $(JOBJECTS) $(JSTUBS)
endif


# We should be careful not to remove top-level class directories.
# This happens if $(PKG_DIR) is empty. If we weren't careful, then the
# $(CLASS_DIR) would be removed. This is almost never what we want.
rclean::
ifeq ($(strip $(PKG_DIR)),)
	@echo "Removing the contents of top-level class directory!"
	rm -r $(CLASS_DIR)/*
else
	rm -r $(CLASS_DIR)/$(PKG_DIR)	
endif

