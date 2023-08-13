#=======================================================================
#
# Makefile : driveIO shared library
#
#-----------------------------------------------------------------------
#
# The following environment variables must be defined:
#   JAVA_HOME : the location of the JDK
#
#=======================================================================

#///////////////////////////////////////////////////////////////////////
#  Variables
#///////////////////////////////////////////////////////////////////////

# Library name
LIB_NAME	= driveio

# Directories
SRC_DIR			= src
DEP_DIR			= dep
OBJ_DIR			= obj
BIN_DIR			= bin
JNI_INC_DIR1	= $(JAVA_HOME)/include
JNI_INC_DIR2	= $(JNI_INC_DIR1)/linux

# Files
EXEC	= $(BIN_DIR)/lib$(LIB_NAME).so

# Tools
CC			= g++
CC_FLAGS	= -I$(JNI_INC_DIR1) -I$(JNI_INC_DIR2) -fPIC -shared -s
LD_FLAGS	= -Wl,-h,$(LIB_NAME)

#///////////////////////////////////////////////////////////////////////
#  Source files and object files
#///////////////////////////////////////////////////////////////////////

SOURCES	= $(wildcard $(SRC_DIR)/*.cc)
OBJECTS	= $(patsubst $(SRC_DIR)/%.cc,$(OBJ_DIR)/%.o,$(SOURCES))

#///////////////////////////////////////////////////////////////////////
#  Targets and rules
#///////////////////////////////////////////////////////////////////////

ifneq ($(findstring all,$(MAKECMDGOALS)),)
    -include $(patsubst $(SRC_DIR)/%.cc,$(DEP_DIR)/%.d,$(SOURCES))
endif

$(DEP_DIR)	:
	mkdir $(DEP_DIR)

$(DEP_DIR)/%.d	: $(SRC_DIR)/%.cc | $(DEP_DIR)
	set -e; rm -f $@.*; \
	$(CC) -MM $(CC_FLAGS) $< > $@.$$$$; \
	sed 's,\($*\)\.o[ :]*,$(OBJ_DIR)/\1.o $@ : ,g' < $@.$$$$ > $@; \
	rm -f $@.$$$$

$(OBJ_DIR)	:
	mkdir $(OBJ_DIR)

$(OBJ_DIR)/%.o	: $(SRC_DIR)/%.cc | $(OBJ_DIR)
	$(CC) -c $(CC_FLAGS) -o $@ $<

$(BIN_DIR)	:
	mkdir $(BIN_DIR)

$(EXEC)	:	$(OBJECTS) | $(BIN_DIR)
	$(CC) $(CC_FLAGS) $(LD_FLAGS) -o $(EXEC) $^

.PHONY	: all
all		: $(EXEC)

.PHONY	: clean
clean	:
	-rm -rf $(DEP_DIR)
	-rm -rf $(OBJ_DIR)
	-rm -f $(EXEC)

#-----------------------------------------------------------------------
