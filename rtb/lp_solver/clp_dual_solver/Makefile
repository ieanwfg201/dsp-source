IDIR = include
ICLPDIR = /usr/share/kritter/external_libraries/lp_solvers/coin-Clp/include/
SRCDIR = src
ODIR = obj
BINDIR = bin
LDIR = /usr/share/kritter/external_libraries/lp_solvers/coin-Clp/lib

CC = g++
CFLAGS = -I$(IDIR) -I$(ICLPDIR)
LDFLAGS = -L$(LDIR)
LFLAGS = -lClp -lCoinUtils -lOsiClp -lOsiCommonTests -lOsi -lz -lbz2 -llapack -lconfig++
WFLAGS = -Wl,-rpath=/usr/share/kritter/external_libraries/lp_solvers/coin-Clp/lib

EXECUTABLE = $(BINDIR)/dual_solver

DEPS = $(ICPLDIR/%.h)

_OBJ = config_utils.o clp_solver.o run_solver.o
OBJ = $(patsubst %, $(ODIR)/%, $(_OBJ))

$(ODIR)/%.o: $(SRCDIR)/%.cpp $(DEPS)
	mkdir -p $(ODIR)
	mkdir -p $(BINDIR)
	$(CC) -c -o $@ $< $(CFLAGS) .

$(EXECUTABLE): $(OBJ)
	$(CC) -o $(EXECUTABLE) $^ $(CFLAGS) $(LDFLAGS) $(LFLAGS) $(WFLAGS)

clean :
	rm -rf $(BINDIR)
	rm -rf $(ODIR)
